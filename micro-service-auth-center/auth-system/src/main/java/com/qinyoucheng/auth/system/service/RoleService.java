package com.qinyoucheng.auth.system.service;

import com.qinyoucheng.auth.common.PageResult;
import com.qinyoucheng.auth.system.dto.RoleDTO;
import com.qinyoucheng.auth.system.vo.RoleVO;

import java.util.List;

public interface RoleService {

    PageResult<RoleVO> listRoles(String roleName, Integer status, String appKey, Integer pageNum, Integer pageSize);

    RoleVO getRoleById(Long id);

    RoleVO createRole(RoleDTO roleDTO);

    RoleVO updateRole(Long id, RoleDTO roleDTO);

    void deleteRole(Long id);

    void assignMenus(Long roleId, List<Long> menuIds);

    List<RoleVO> getRolesByUserId(Long userId);

    List<RoleVO> listAllRoles(String appKey);
}
