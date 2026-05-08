package com.qinyoucheng.supply.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qinyoucheng.supply.common.BusinessException;
import com.qinyoucheng.supply.common.PageResult;
import com.qinyoucheng.supply.common.ResultCode;
import com.qinyoucheng.supply.dto.ParkEntryDTO;
import com.qinyoucheng.supply.entity.BookingOrder;
import com.qinyoucheng.supply.entity.ParkEntry;
import com.qinyoucheng.supply.mapper.BookingOrderMapper;
import com.qinyoucheng.supply.mapper.ParkEntryMapper;
import com.qinyoucheng.supply.service.ParkEntryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParkEntryServiceImpl implements ParkEntryService {

    private final ParkEntryMapper parkEntryMapper;
    private final BookingOrderMapper bookingOrderMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createEntry(ParkEntryDTO dto) {
        BookingOrder booking = bookingOrderMapper.selectById(dto.getBookingId());
        if (booking == null) {
            throw new BusinessException(ResultCode.BOOKING_NOT_FOUND);
        }
        if (booking.getStatus() != 1) {
            throw new BusinessException("预约订单未确认，无法创建入园登记");
        }

        LambdaQueryWrapper<ParkEntry> existWrapper = new LambdaQueryWrapper<>();
        existWrapper.eq(ParkEntry::getBookingId, dto.getBookingId())
                .ne(ParkEntry::getStatus, 2);
        if (parkEntryMapper.selectCount(existWrapper) > 0) {
            throw new BusinessException("该预约已存在有效的入园登记");
        }

        ParkEntry entry = new ParkEntry();
        entry.setBookingId(dto.getBookingId());
        entry.setSupplierId(booking.getSupplierId());
        entry.setEntryNo(generateEntryNo());
        entry.setVehicleNo(dto.getVehicleNo() != null ? dto.getVehicleNo() : booking.getVehicleNo());
        entry.setDriverName(dto.getDriverName() != null ? dto.getDriverName() : booking.getDriverName());
        entry.setGateNo(dto.getGateNo());
        entry.setStatus(0);
        entry.setRemark(dto.getRemark());
        parkEntryMapper.insert(entry);

        return entry.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmEntry(Long id) {
        ParkEntry entry = parkEntryMapper.selectById(id);
        if (entry == null) {
            throw new BusinessException(ResultCode.PARK_ENTRY_NOT_FOUND);
        }
        if (entry.getStatus() != 0) {
            throw new BusinessException("当前状态不允许确认入园");
        }

        entry.setStatus(1);
        entry.setEntryTime(LocalDateTime.now());
        parkEntryMapper.updateById(entry);

        BookingOrder booking = bookingOrderMapper.selectById(entry.getBookingId());
        if (booking != null && booking.getStatus() == 1) {
            booking.setStatus(2);
            bookingOrderMapper.updateById(booking);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmExit(Long id) {
        ParkEntry entry = parkEntryMapper.selectById(id);
        if (entry == null) {
            throw new BusinessException(ResultCode.PARK_ENTRY_NOT_FOUND);
        }
        if (entry.getStatus() != 1) {
            throw new BusinessException("当前状态不允许确认离园");
        }

        entry.setStatus(2);
        entry.setExitTime(LocalDateTime.now());
        parkEntryMapper.updateById(entry);
    }

    @Override
    public PageResult<ParkEntry> pageList(Integer pageNum, Integer pageSize, Long bookingId, Integer status) {
        Page<ParkEntry> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ParkEntry> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(bookingId != null, ParkEntry::getBookingId, bookingId);
        wrapper.eq(status != null, ParkEntry::getStatus, status);
        wrapper.orderByDesc(ParkEntry::getCreateTime);

        Page<ParkEntry> result = parkEntryMapper.selectPage(page, wrapper);
        return PageResult.of(result.getTotal(), pageNum, pageSize, result.getRecords());
    }

    private String generateEntryNo() {
        return "PE" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + IdUtil.simpleUUID().substring(0, 6).toUpperCase();
    }
}
