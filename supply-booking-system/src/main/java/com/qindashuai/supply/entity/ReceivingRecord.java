package com.qindashuai.supply.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("receiving_record")
public class ReceivingRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long bookingId;

    private Long supplierId;

    private Long entryId;

    private String receivingNo;

    private String goodsType;

    private BigDecimal goodsQuantity;

    private String receivingPerson;

    private LocalDateTime receivingTime;

    private Integer qualityStatus;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
