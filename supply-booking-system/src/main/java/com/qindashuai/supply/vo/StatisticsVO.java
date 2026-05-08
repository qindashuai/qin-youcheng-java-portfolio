package com.qindashuai.supply.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class StatisticsVO {

    private Long totalBookings;

    private Long confirmedBookings;

    private Long completedBookings;

    private Long cancelledBookings;

    private BigDecimal totalGoodsQuantity;

    private Long totalSuppliers;

    private Long activeSuppliers;

    private Long expiringQualifications;

    private Map<String, Long> bookingsByStatus;

    private Map<String, BigDecimal> goodsByType;

    private Map<String, Long> bookingsByDate;

    private Map<String, Long> topSuppliers;
}
