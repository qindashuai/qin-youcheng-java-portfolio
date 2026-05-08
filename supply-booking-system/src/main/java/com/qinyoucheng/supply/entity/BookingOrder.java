package com.qinyoucheng.supply.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("booking_order")
public class BookingOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String bookingNo;

    private Long supplierId;

    private Long timeSlotId;

    private LocalDate bookingDate;

    private String bookingTime;

    private String vehicleNo;

    private String driverName;

    private String driverPhone;

    private String goodsType;

    private BigDecimal goodsQuantity;

    private Integer status;

    private LocalDateTime confirmTime;

    private String cancelReason;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
