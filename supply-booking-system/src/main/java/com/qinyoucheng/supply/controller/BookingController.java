package com.qinyoucheng.supply.controller;

import com.qinyoucheng.supply.common.PageResult;
import com.qinyoucheng.supply.common.Result;
import com.qinyoucheng.supply.dto.BookingDTO;
import com.qinyoucheng.supply.entity.TimeSlot;
import com.qinyoucheng.supply.mapper.TimeSlotMapper;
import com.qinyoucheng.supply.service.BookingService;
import com.qinyoucheng.supply.vo.BookingVO;
import com.qinyoucheng.supply.vo.TimeSlotVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final TimeSlotMapper timeSlotMapper;

    @PostMapping
    public Result<Long> create(@Valid @RequestBody BookingDTO dto) {
        return Result.success(bookingService.createBooking(dto));
    }

    @PutMapping("/{id}/confirm")
    public Result<Void> confirm(@PathVariable Long id) {
        bookingService.confirmBooking(id);
        return Result.success();
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id, @RequestParam(required = false) String reason) {
        bookingService.cancelBooking(id, reason);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<BookingVO> getById(@PathVariable Long id) {
        return Result.success(bookingService.getBookingById(id));
    }

    @GetMapping("/page")
    public Result<PageResult<BookingVO>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String bookingDate) {
        return Result.success(bookingService.pageList(pageNum, pageSize, supplierId, status, bookingDate));
    }

    @GetMapping("/time-slots")
    public Result<List<TimeSlotVO>> getTimeSlots(@RequestParam String date) {
        LocalDate slotDate = LocalDate.parse(date);
        List<TimeSlot> slots = timeSlotMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<TimeSlot>()
                        .eq(TimeSlot::getSlotDate, slotDate)
                        .eq(TimeSlot::getStatus, 1)
                        .orderByAsc(TimeSlot::getStartTime));
        List<TimeSlotVO> voList = slots.stream().map(TimeSlotVO::fromEntity).collect(Collectors.toList());
        return Result.success(voList);
    }
}
