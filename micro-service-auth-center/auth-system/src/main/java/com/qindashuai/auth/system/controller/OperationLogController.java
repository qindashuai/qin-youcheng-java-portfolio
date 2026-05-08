package com.qindashuai.auth.system.controller;

import com.qindashuai.auth.common.PageResult;
import com.qindashuai.auth.common.Result;
import com.qindashuai.auth.system.service.OperationLogService;
import com.qindashuai.auth.system.vo.OperationLogVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/api/v1/logs")
public class OperationLogController {

    @Resource
    private OperationLogService operationLogService;

    @GetMapping
    public Result<PageResult<OperationLogVO>> list(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false, defaultValue = "default_system") String appKey,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<OperationLogVO> result = operationLogService.listLogs(username, operationType, appKey, pageNum, pageSize);
        return Result.success(result);
    }
}
