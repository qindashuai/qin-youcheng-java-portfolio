package com.qindashuai.auth.system.service;

import com.qindashuai.auth.common.PageResult;
import com.qindashuai.auth.system.dto.DataPermissionDTO;
import com.qindashuai.auth.system.entity.SysDataPermission;

public interface DataPermissionService {

    PageResult<SysDataPermission> listDataPermissions(Long roleId, String appKey, Integer pageNum, Integer pageSize);

    SysDataPermission getDataPermissionById(Long id);

    SysDataPermission createDataPermission(DataPermissionDTO dto);

    SysDataPermission updateDataPermission(Long id, DataPermissionDTO dto);

    void deleteDataPermission(Long id);

    SysDataPermission getDataPermissionByRoleId(Long roleId, String appKey);
}
