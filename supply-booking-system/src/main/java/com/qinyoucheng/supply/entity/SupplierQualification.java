package com.qinyoucheng.supply.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("supplier_qualification")
public class SupplierQualification {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long supplierId;

    private String qualificationType;

    private String qualificationName;

    private String certificateNo;

    private LocalDate issueDate;

    private LocalDate expireDate;

    private Integer status;

    private String fileUrl;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
