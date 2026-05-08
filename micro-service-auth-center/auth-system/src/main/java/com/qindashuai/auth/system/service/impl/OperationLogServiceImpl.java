package com.qindashuai.auth.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qindashuai.auth.common.PageResult;
import com.qindashuai.auth.system.entity.SysOperationLog;
import com.qindashuai.auth.system.mapper.SysOperationLogMapper;
import com.qindashuai.auth.system.service.OperationLogService;
import com.qindashuai.auth.system.vo.OperationLogVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Resource
    private SysOperationLogMapper operationLogMapper;

    @Override
    public PageResult<OperationLogVO> listLogs(String username, String operationType, String appKey, Integer pageNum, Integer pageSize) {
        Page<SysOperationLog> page = new Page<>(pageNum, pageSize);
        IPage<SysOperationLog> result = operationLogMapper.selectLogPage(page, username, operationType, appKey);
        List<OperationLogVO> voList = result.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        return PageResult.of(result.getTotal(), pageNum, pageSize, voList);
    }

    @Override
    public void saveLog(SysOperationLog operationLog) {
        operationLogMapper.insert(operationLog);
    }

    private OperationLogVO convertToVO(SysOperationLog entity) {
        OperationLogVO vo = new OperationLogVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
