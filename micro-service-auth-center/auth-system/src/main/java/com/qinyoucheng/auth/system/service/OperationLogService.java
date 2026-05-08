package com.qinyoucheng.auth.system.service;

import com.qinyoucheng.auth.common.PageResult;
import com.qinyoucheng.auth.system.entity.SysOperationLog;
import com.qinyoucheng.auth.system.vo.OperationLogVO;

public interface OperationLogService {

    PageResult<OperationLogVO> listLogs(String username, String operationType, String appKey, Integer pageNum, Integer pageSize);

    void saveLog(SysOperationLog operationLog);
}
