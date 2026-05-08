package com.qindashuai.supply.controller;

import com.qindashuai.supply.common.Result;
import com.qindashuai.supply.dto.StatisticsDTO;
import com.qindashuai.supply.service.StatisticsService;
import com.qindashuai.supply.vo.StatisticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/overview")
    public Result<StatisticsVO> overview(StatisticsDTO dto) {
        return Result.success(statisticsService.getOverviewStatistics(dto));
    }

    @GetMapping("/booking")
    public Result<StatisticsVO> booking(StatisticsDTO dto) {
        return Result.success(statisticsService.getBookingStatistics(dto));
    }

    @GetMapping("/supplier")
    public Result<StatisticsVO> supplier() {
        return Result.success(statisticsService.getSupplierStatistics());
    }
}
