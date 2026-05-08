package com.qindashuai.supply.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StatisticsDTO {

    private LocalDate startDate;

    private LocalDate endDate;

    private Long supplierId;

    private Integer status;
}
