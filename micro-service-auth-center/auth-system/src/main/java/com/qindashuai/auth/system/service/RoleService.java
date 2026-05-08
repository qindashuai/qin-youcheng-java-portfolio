package com.qindashuai.auth.system.service;

import com.qindashuai.auth.common.PageResult;
import com.qindashuai.auth.system.dto.RoleDTO;
import com.qindashuai.auth.system.vo.RoleVO;

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
