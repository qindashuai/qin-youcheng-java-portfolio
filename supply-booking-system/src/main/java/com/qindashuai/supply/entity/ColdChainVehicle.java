package com.qindashuai.supply.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("cold_chain_vehicle")
public class ColdChainVehicle {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long supplierId;

    private String vehicleNo;

    private String vehicleType;

    private String temperatureRange;

    private BigDecimal vehicleCapacity;

    private LocalDate inspectionExpireDate;

    private Integer status;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
