package com.qinyoucheng.supply.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("supplier")
public class Supplier {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String supplierCode;

    private String supplierName;

    private String contactPerson;

    private String contactPhone;

    private String businessScope;

    private String address;

    private Integer status;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
