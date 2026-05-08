package com.qinyoucheng.auth.system.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperationLogVO {

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
    private LocalDateTime createTime;
}
