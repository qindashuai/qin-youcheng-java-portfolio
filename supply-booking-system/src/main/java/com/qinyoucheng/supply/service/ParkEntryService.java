package com.qinyoucheng.supply.service;

import com.qinyoucheng.supply.common.PageResult;
import com.qinyoucheng.supply.dto.ParkEntryDTO;
import com.qinyoucheng.supply.entity.ParkEntry;
import com.qinyoucheng.supply.vo.BookingVO;

public interface ParkEntryService {

    Long createEntry(ParkEntryDTO dto);

    void confirmEntry(Long id);

    void confirmExit(Long id);

    PageResult<ParkEntry> pageList(Integer pageNum, Integer pageSize, Long bookingId, Integer status);
}
