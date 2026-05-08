package com.qinyoucheng.auth.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_operation_log")
public class SysOperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String traceId;

    private Long userId;

    private String username;

    private String operationType;

    private String operationDesc;

    private String requestMethod;

    private String requestUrl;

    private String requestParams;

    private String responseResult;

    private String ip;

    private Long duration;

    private Integer status;

    private String errorMsg;

    private String appKey;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
