package com.qindashuai.supply.service;

import com.qindashuai.supply.common.PageResult;
import com.qindashuai.supply.dto.ParkEntryDTO;
import com.qindashuai.supply.entity.ParkEntry;
import com.qindashuai.supply.vo.BookingVO;

public interface ParkEntryService {

    Long createEntry(ParkEntryDTO dto);

    void confirmEntry(Long id);

    void confirmExit(Long id);

    PageResult<ParkEntry> pageList(Integer pageNum, Integer pageSize, Long bookingId, Integer status);
}
