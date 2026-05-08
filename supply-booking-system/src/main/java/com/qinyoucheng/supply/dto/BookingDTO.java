package com.qinyoucheng.supply.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class BookingDTO {

    @NotNull(message = "供应商ID不能为空")
    private Long supplierId;

    @NotNull(message = "时间段ID不能为空")
    private Long timeSlotId;

    private String vehicleNo;

    private String driverName;

    private String driverPhone;

    private String goodsType;

    private BigDecimal goodsQuantity;

    private String remark;
}
