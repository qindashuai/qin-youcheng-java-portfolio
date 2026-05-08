package com.qindashuai.supply.service;

import com.qindashuai.supply.common.PageResult;
import com.qindashuai.supply.dto.BookingDTO;
import com.qindashuai.supply.entity.BookingOrder;
import com.qindashuai.supply.vo.BookingVO;

public interface BookingService {

    Long createBooking(BookingDTO dto);

    void confirmBooking(Long id);

    void cancelBooking(Long id, String reason);

    BookingVO getBookingById(Long id);

    PageResult<BookingVO> pageList(Integer pageNum, Integer pageSize, Long supplierId, Integer status, String bookingDate);
}
