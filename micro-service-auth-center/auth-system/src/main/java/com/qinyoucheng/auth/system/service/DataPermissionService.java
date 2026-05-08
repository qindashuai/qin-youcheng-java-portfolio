package com.qinyoucheng.auth.system.service;

import com.qinyoucheng.auth.common.PageResult;
import com.qinyoucheng.auth.system.dto.DataPermissionDTO;
import com.qinyoucheng.auth.system.entity.SysDataPermission;

public interface DataPermissionService {

    PageResult<SysDataPermission> listDataPermissions(Long roleId, String appKey, Integer pageNum, Integer pageSize);

    SysDataPermission getDataPermissionById(Long id);

    SysDataPermission createDataPermission(DataPermissionDTO dto);

    SysDataPermission updateDataPermission(Long id, DataPermissionDTO dto);

    void deleteDataPermission(Long id);

    SysDataPermission getDataPermissionByRoleId(Long roleId, String appKey);
}
