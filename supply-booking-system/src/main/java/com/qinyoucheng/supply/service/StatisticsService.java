package com.qinyoucheng.supply.service;

import com.qinyoucheng.supply.dto.StatisticsDTO;
import com.qinyoucheng.supply.vo.StatisticsVO;

public interface StatisticsService {

    StatisticsVO getOverviewStatistics(StatisticsDTO dto);

    StatisticsVO getBookingStatistics(StatisticsDTO dto);

    StatisticsVO getSupplierStatistics();
}
