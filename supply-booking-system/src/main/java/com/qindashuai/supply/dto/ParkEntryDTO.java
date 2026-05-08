package com.qindashuai.supply.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ParkEntryDTO {

    @NotNull(message = "预约订单ID不能为空")
    private Long bookingId;

    private String vehicleNo;

    private String driverName;

    private String gateNo;

    private String remark;
}
