package com.qindashuai.auth.system.service;

import com.qindashuai.auth.common.PageResult;
import com.qindashuai.auth.system.entity.SysOperationLog;
import com.qindashuai.auth.system.vo.OperationLogVO;

public interface OperationLogService {

    PageResult<OperationLogVO> listLogs(String username, String operationType, String appKey, Integer pageNum, Integer pageSize);

    void saveLog(SysOperationLog operationLog);
}
