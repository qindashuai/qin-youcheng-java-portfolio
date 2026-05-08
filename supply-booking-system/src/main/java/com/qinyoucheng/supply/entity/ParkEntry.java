package com.qinyoucheng.supply.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("park_entry")
public class ParkEntry {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long bookingId;

    private Long supplierId;

    private String entryNo;

    private String vehicleNo;

    private String driverName;

    private LocalDateTime entryTime;

    private LocalDateTime exitTime;

    private String gateNo;

    private Integer status;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
