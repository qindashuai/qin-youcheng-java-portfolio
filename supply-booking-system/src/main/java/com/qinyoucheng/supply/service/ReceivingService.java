package com.qinyoucheng.supply.service;

import com.qinyoucheng.supply.common.PageResult;
import com.qinyoucheng.supply.entity.ReceivingRecord;

public interface ReceivingService {

    Long createReceivingRecord(ReceivingRecord record);

    void updateReceivingRecord(ReceivingRecord record);

    ReceivingRecord getReceivingById(Long id);

    PageResult<ReceivingRecord> pageList(Integer pageNum, Integer pageSize, Long bookingId, Long supplierId);
}
