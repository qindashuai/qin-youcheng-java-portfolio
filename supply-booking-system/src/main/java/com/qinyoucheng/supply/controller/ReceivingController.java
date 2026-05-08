package com.qinyoucheng.supply.controller;

import com.qinyoucheng.supply.common.PageResult;
import com.qinyoucheng.supply.common.Result;
import com.qinyoucheng.supply.entity.ReceivingRecord;
import com.qinyoucheng.supply.service.ReceivingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/receiving")
@RequiredArgsConstructor
public class ReceivingController {

    private final ReceivingService receivingService;

    @PostMapping
    public Result<Long> create(@RequestBody ReceivingRecord record) {
        return Result.success(receivingService.createReceivingRecord(record));
    }

    @PutMapping
    public Result<Void> update(@RequestBody ReceivingRecord record) {
        receivingService.updateReceivingRecord(record);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<ReceivingRecord> getById(@PathVariable Long id) {
        return Result.success(receivingService.getReceivingById(id));
    }

    @GetMapping("/page")
    public Result<PageResult<ReceivingRecord>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long bookingId,
            @RequestParam(required = false) Long supplierId) {
        return Result.success(receivingService.pageList(pageNum, pageSize, bookingId, supplierId));
    }
}
