package com.qindashuai.supply.service;

import com.qindashuai.supply.dto.StatisticsDTO;
import com.qindashuai.supply.vo.StatisticsVO;

public interface StatisticsService {

    StatisticsVO getOverviewStatistics(StatisticsDTO dto);

    StatisticsVO getBookingStatistics(StatisticsDTO dto);

    StatisticsVO getSupplierStatistics();
}
