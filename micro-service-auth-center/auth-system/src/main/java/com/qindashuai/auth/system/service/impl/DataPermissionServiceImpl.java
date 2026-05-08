package com.qindashuai.auth.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qindashuai.auth.common.BusinessException;
import com.qindashuai.auth.common.PageResult;
import com.qindashuai.auth.common.ResultCode;
import com.qindashuai.auth.system.dto.DataPermissionDTO;
import com.qindashuai.auth.system.entity.SysDataPermission;
import com.qindashuai.auth.system.mapper.SysDataPermissionMapper;
import com.qindashuai.auth.system.service.DataPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class DataPermissionServiceImpl implements DataPermissionService {

    @Resource
    private SysDataPermissionMapper dataPermissionMapper;

    @Override
    public PageResult<SysDataPermission> listDataPermissions(Long roleId, String appKey, Integer pageNum, Integer pageSize) {
        Page<SysDataPermission> page = new Page<>(pageNum, pageSize);
        IPage<SysDataPermission> result = dataPermissionMapper.selectPageWithDetail(page, roleId, appKey);
        return PageResult.of(result.getTotal(), pageNum, pageSize, result.getRecords());
    }

    @Override
    public SysDataPermission getDataPermissionById(Long id) {
        SysDataPermission permission = dataPermissionMapper.selectById(id);
        if (permission == null) {
            throw new BusinessException(ResultCode.FAIL.getCode(), "数据权限不存在");
        }
        return permission;
    }

    @Override
    public SysDataPermission createDataPermission(DataPermissionDTO dto) {
        SysDataPermission permission = new SysDataPermission();
        permission.setRoleId(dto.getRoleId());
        permission.setPermissionType(dto.getPermissionType());
        permission.setDeptId(dto.getDeptId());
        permission.setCustomCondition(dto.getCustomCondition());
        permission.setAppKey(dto.getAppKey());
        dataPermissionMapper.insert(permission);
        log.info("创建数据权限: id={}, roleId={}", permission.getId(), permission.getRoleId());
        return permission;
    }

    @Override
    public SysDataPermission updateDataPermission(Long id, DataPermissionDTO dto) {
        SysDataPermission permission = dataPermissionMapper.selectById(id);
        if (permission == null) {
            throw new BusinessException(ResultCode.FAIL.getCode(), "数据权限不存在");
        }
        if (dto.getPermissionType() != null) permission.setPermissionType(dto.getPermissionType());
        if (dto.getDeptId() != null) permission.setDeptId(dto.getDeptId());
        if (dto.getCustomCondition() != null) permission.setCustomCondition(dto.getCustomCondition());
        dataPermissionMapper.updateById(permission);
        log.info("更新数据权限: id={}", id);
        return permission;
    }

    @Override
    public void deleteDataPermission(Long id) {
        SysDataPermission permission = dataPermissionMapper.selectById(id);
        if (permission == null) {
            throw new BusinessException(ResultCode.FAIL.getCode(), "数据权限不存在");
        }
        dataPermissionMapper.deleteById(id);
        log.info("删除数据权限: id={}", id);
    }

    @Override
    public SysDataPermission getDataPermissionByRoleId(Long roleId, String appKey) {
        return dataPermissionMapper.selectByRoleId(roleId, appKey);
    }
}
