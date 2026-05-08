package com.qindashuai.supply.controller;

import com.qindashuai.supply.common.PageResult;
import com.qindashuai.supply.common.Result;
import com.qindashuai.supply.dto.ParkEntryDTO;
import com.qindashuai.supply.entity.ParkEntry;
import com.qindashuai.supply.service.ParkEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/park-entry")
@RequiredArgsConstructor
public class ParkEntryController {

    private final ParkEntryService parkEntryService;

    @PostMapping
    public Result<Long> create(@Valid @RequestBody ParkEntryDTO dto) {
        return Result.success(parkEntryService.createEntry(dto));
    }

    @PutMapping("/{id}/entry")
    public Result<Void> confirmEntry(@PathVariable Long id) {
        parkEntryService.confirmEntry(id);
        return Result.success();
    }

    @PutMapping("/{id}/exit")
    public Result<Void> confirmExit(@PathVariable Long id) {
        parkEntryService.confirmExit(id);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult<ParkEntry>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long bookingId,
            @RequestParam(required = false) Integer status) {
        return Result.success(parkEntryService.pageList(pageNum, pageSize, bookingId, status));
    }
}
