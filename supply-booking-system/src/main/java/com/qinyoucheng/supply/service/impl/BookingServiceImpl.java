package com.qinyoucheng.supply.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qinyoucheng.supply.common.BusinessException;
import com.qinyoucheng.supply.common.PageResult;
import com.qinyoucheng.supply.common.ResultCode;
import com.qinyoucheng.supply.config.RabbitMQConfig;
import com.qinyoucheng.supply.dto.BookingDTO;
import com.qinyoucheng.supply.entity.BookingOrder;
import com.qinyoucheng.supply.entity.Supplier;
import com.qinyoucheng.supply.entity.TimeSlot;
import com.qinyoucheng.supply.mapper.BookingOrderMapper;
import com.qinyoucheng.supply.mapper.SupplierMapper;
import com.qinyoucheng.supply.mapper.TimeSlotMapper;
import com.qinyoucheng.supply.service.BookingService;
import com.qinyoucheng.supply.util.RedisUtil;
import com.qinyoucheng.supply.vo.BookingVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingOrderMapper bookingOrderMapper;
    private final TimeSlotMapper timeSlotMapper;
    private final SupplierMapper supplierMapper;
    private final RedisUtil redisUtil;
    private final RabbitTemplate rabbitTemplate;

    private static final String BOOKING_LOCK_KEY = "supply:booking:lock:";
    private static final long LOCK_EXPIRE_SECONDS = 10;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createBooking(BookingDTO dto) {
        String lockKey = BOOKING_LOCK_KEY + dto.getTimeSlotId();
        boolean locked = redisUtil.setIfAbsent(lockKey, "1", LOCK_EXPIRE_SECONDS, TimeUnit.SECONDS);
        if (!locked) {
            throw new BusinessException("系统繁忙，请稍后重试");
        }

        try {
            TimeSlot timeSlot = timeSlotMapper.selectById(dto.getTimeSlotId());
            if (timeSlot == null) {
                throw new BusinessException(ResultCode.TIMESLOT_NOT_FOUND);
            }
            if (timeSlot.getStatus() != 1) {
                throw new BusinessException("该时间段已禁用");
            }
            if (timeSlot.getCurrentBooked() >= timeSlot.getMaxCapacity()) {
                throw new BusinessException(ResultCode.BOOKING_SLOT_FULL);
            }

            Supplier supplier = supplierMapper.selectById(dto.getSupplierId());
            if (supplier == null) {
                throw new BusinessException(ResultCode.SUPPLIER_NOT_FOUND);
            }
            if (supplier.getStatus() != 1) {
                throw new BusinessException(ResultCode.SUPPLIER_DISABLED);
            }

            LambdaQueryWrapper<BookingOrder> conflictWrapper = new LambdaQueryWrapper<>();
            conflictWrapper.eq(BookingOrder::getSupplierId, dto.getSupplierId())
                    .eq(BookingOrder::getTimeSlotId, dto.getTimeSlotId())
                    .ne(BookingOrder::getStatus, 4);
            if (bookingOrderMapper.selectCount(conflictWrapper) > 0) {
                throw new BusinessException(ResultCode.BOOKING_CONFLICT);
            }

            BookingOrder order = new BookingOrder();
            BeanUtil.copyProperties(dto, order);
            order.setBookingNo(generateBookingNo());
            order.setBookingDate(timeSlot.getSlotDate());
            order.setBookingTime(timeSlot.getStartTime() + "-" + timeSlot.getEndTime());
            order.setStatus(0);
            bookingOrderMapper.insert(order);

            timeSlot.setCurrentBooked(timeSlot.getCurrentBooked() + 1);
            int updateRows = timeSlotMapper.updateById(timeSlot);
            if (updateRows == 0) {
                throw new BusinessException(ResultCode.BOOKING_SLOT_FULL);
            }

            return order.getId();
        } finally {
            redisUtil.delete(lockKey);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmBooking(Long id) {
        BookingOrder order = bookingOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.BOOKING_NOT_FOUND);
        }
        if (order.getStatus() != 0) {
            throw new BusinessException(ResultCode.BOOKING_CANNOT_CONFIRM);
        }

        order.setStatus(1);
        order.setConfirmTime(LocalDateTime.now());
        bookingOrderMapper.updateById(order);

        Map<String, Object> linkageMsg = new HashMap<>();
        linkageMsg.put("bookingId", order.getId());
        linkageMsg.put("supplierId", order.getSupplierId());
        linkageMsg.put("bookingNo", order.getBookingNo());
        linkageMsg.put("vehicleNo", order.getVehicleNo());
        linkageMsg.put("driverName", order.getDriverName());
        linkageMsg.put("action", "CONFIRMED");

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.BOOKING_LINKAGE_EXCHANGE,
                RabbitMQConfig.BOOKING_LINKAGE_ROUTING_KEY,
                linkageMsg
        );

        log.info("预约已确认，发送联动消息: bookingId={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelBooking(Long id, String reason) {
        BookingOrder order = bookingOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.BOOKING_NOT_FOUND);
        }
        if (order.getStatus() == 3 || order.getStatus() == 4) {
            throw new BusinessException(ResultCode.BOOKING_CANNOT_CANCEL);
        }

        order.setStatus(4);
        order.setCancelReason(reason);
        bookingOrderMapper.updateById(order);

        TimeSlot timeSlot = timeSlotMapper.selectById(order.getTimeSlotId());
        if (timeSlot != null && timeSlot.getCurrentBooked() > 0) {
            timeSlot.setCurrentBooked(timeSlot.getCurrentBooked() - 1);
            timeSlotMapper.updateById(timeSlot);
        }
    }

    @Override
    public BookingVO getBookingById(Long id) {
        BookingOrder order = bookingOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.BOOKING_NOT_FOUND);
        }
        return convertToVO(order);
    }

    @Override
    public PageResult<BookingVO> pageList(Integer pageNum, Integer pageSize, Long supplierId, Integer status, String bookingDate) {
        Page<BookingOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BookingOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(supplierId != null, BookingOrder::getSupplierId, supplierId);
        wrapper.eq(status != null, BookingOrder::getStatus, status);
        wrapper.eq(StringUtils.hasText(bookingDate), BookingOrder::getBookingDate, LocalDate.parse(bookingDate));
        wrapper.orderByDesc(BookingOrder::getCreateTime);

        Page<BookingOrder> result = bookingOrderMapper.selectPage(page, wrapper);
        List<BookingVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(result.getTotal(), pageNum, pageSize, voList);
    }

    private BookingVO convertToVO(BookingOrder order) {
        BookingVO vo = new BookingVO();
        BeanUtil.copyProperties(order, vo);
        vo.setStatusDesc(BookingVO.getStatusDesc(order.getStatus()));

        Supplier supplier = supplierMapper.selectById(order.getSupplierId());
        if (supplier != null) {
            vo.setSupplierName(supplier.getSupplierName());
        }
        return vo;
    }

    private String generateBookingNo() {
        return "BK" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + IdUtil.simpleUUID().substring(0, 6).toUpperCase();
    }
}
