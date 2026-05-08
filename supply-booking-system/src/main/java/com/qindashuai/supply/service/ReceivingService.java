package com.qindashuai.supply.service;

import com.qindashuai.supply.common.PageResult;
import com.qindashuai.supply.entity.ReceivingRecord;

public interface ReceivingService {

    Long createReceivingRecord(ReceivingRecord record);

    void updateReceivingRecord(ReceivingRecord record);

    ReceivingRecord getReceivingById(Long id);

    PageResult<ReceivingRecord> pageList(Integer pageNum, Integer pageSize, Long bookingId, Long supplierId);
}
