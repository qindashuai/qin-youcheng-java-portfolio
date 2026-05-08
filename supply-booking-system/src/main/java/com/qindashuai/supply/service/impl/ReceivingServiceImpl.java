package com.qindashuai.supply.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qindashuai.supply.common.BusinessException;
import com.qindashuai.supply.common.PageResult;
import com.qindashuai.supply.common.ResultCode;
import com.qindashuai.supply.entity.BookingOrder;
import com.qindashuai.supply.entity.ReceivingRecord;
import com.qindashuai.supply.mapper.BookingOrderMapper;
import com.qindashuai.supply.mapper.ReceivingRecordMapper;
import com.qindashuai.supply.service.ReceivingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceivingServiceImpl implements ReceivingService {

    private final ReceivingRecordMapper receivingRecordMapper;
    private final BookingOrderMapper bookingOrderMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createReceivingRecord(ReceivingRecord record) {
        BookingOrder booking = bookingOrderMapper.selectById(record.getBookingId());
        if (booking == null) {
            throw new BusinessException(ResultCode.BOOKING_NOT_FOUND);
        }

        record.setSupplierId(booking.getSupplierId());
        record.setReceivingNo(generateReceivingNo());
        if (record.getQualityStatus() == null) {
            record.setQualityStatus(2);
        }
        receivingRecordMapper.insert(record);

        if (booking.getStatus() == 2) {
            booking.setStatus(3);
            bookingOrderMapper.updateById(booking);
        }

        return record.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateReceivingRecord(ReceivingRecord record) {
        ReceivingRecord existing = receivingRecordMapper.selectById(record.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.RECEIVING_NOT_FOUND);
        }
        receivingRecordMapper.updateById(record);
    }

    @Override
    public ReceivingRecord getReceivingById(Long id) {
        ReceivingRecord record = receivingRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(ResultCode.RECEIVING_NOT_FOUND);
        }
        return record;
    }

    @Override
    public PageResult<ReceivingRecord> pageList(Integer pageNum, Integer pageSize, Long bookingId, Long supplierId) {
        Page<ReceivingRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ReceivingRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(bookingId != null, ReceivingRecord::getBookingId, bookingId);
        wrapper.eq(supplierId != null, ReceivingRecord::getSupplierId, supplierId);
        wrapper.orderByDesc(ReceivingRecord::getCreateTime);

        Page<ReceivingRecord> result = receivingRecordMapper.selectPage(page, wrapper);
        return PageResult.of(result.getTotal(), pageNum, pageSize, result.getRecords());
    }

    private String generateReceivingNo() {
        return "RC" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + IdUtil.simpleUUID().substring(0, 6).toUpperCase();
    }
}
