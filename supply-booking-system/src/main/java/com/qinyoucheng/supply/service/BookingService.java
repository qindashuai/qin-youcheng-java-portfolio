package com.qinyoucheng.supply.service;

import com.qinyoucheng.supply.common.PageResult;
import com.qinyoucheng.supply.dto.BookingDTO;
import com.qinyoucheng.supply.entity.BookingOrder;
import com.qinyoucheng.supply.vo.BookingVO;

public interface BookingService {

    Long createBooking(BookingDTO dto);

    void confirmBooking(Long id);

    void cancelBooking(Long id, String reason);

    BookingVO getBookingById(Long id);

    PageResult<BookingVO> pageList(Integer pageNum, Integer pageSize, Long supplierId, Integer status, String bookingDate);
}
