package com.qindashuai.supply.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("time_slot")
public class TimeSlot {

    @TableId(type = IdType.AUTO)
    private Long id;

    private LocalDate slotDate;

    private String startTime;

    private String endTime;

    private Integer maxCapacity;

    private Integer currentBooked;

    private Integer status;

    @Version
    private Integer version;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
