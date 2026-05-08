package com.qindashuai.auth.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qindashuai.auth.common.BusinessException;
import com.qindashuai.auth.common.PageResult;
import com.qindashuai.auth.common.ResultCode;
import com.qindashuai.auth.system.dto.RoleDTO;
import com.qindashuai.auth.system.entity.SysMenu;
import com.qindashuai.auth.system.entity.SysRole;
import com.qindashuai.auth.system.entity.SysRoleMenu;
import com.qindashuai.auth.system.mapper.SysMenuMapper;
import com.qindashuai.auth.system.mapper.SysRoleMapper;
import com.qindashuai.auth.system.mapper.SysRoleMenuMapper;
import com.qindashuai.auth.system.service.RoleService;
import com.qindashuai.auth.system.vo.RoleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    private SysRoleMapper roleMapper;

    @Resource
    private SysRoleMenuMapper roleMenuMapper;

    @Resource
    private SysMenuMapper menuMapper;

    @Override
    public PageResult<RoleVO> listRoles(String roleName, Integer status, String appKey, Integer pageNum, Integer pageSize) {
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        IPage<SysRole> rolePage = roleMapper.selectRolePage(page, roleName, status, appKey);
        List<RoleVO> voList = rolePage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        return PageResult.of(rolePage.getTotal(), pageNum, pageSize, voList);
    }

    @Override
    public RoleVO getRoleById(Long id) {
        SysRole role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.ROLE_NOT_FOUND);
        }
        RoleVO vo = convertToVO(role);
        List<SysMenu> menus = menuMapper.selectMenusByRoleId(id);
        vo.setMenuIds(menus.stream().map(SysMenu::getId).collect(Collectors.toList()));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoleVO createRole(RoleDTO roleDTO) {
        SysRole role = new SysRole();
        BeanUtils.copyProperties(roleDTO, role);
        role.setStatus(roleDTO.getStatus() != null ? roleDTO.getStatus() : 1);
        roleMapper.insert(role);

        if (roleDTO.getMenuIds() != null && !roleDTO.getMenuIds().isEmpty()) {
            assignMenus(role.getId(), roleDTO.getMenuIds());
        }

        log.info("创建角色: roleId={}, roleName={}", role.getId(), role.getRoleName());
        return convertToVO(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoleVO updateRole(Long id, RoleDTO roleDTO) {
        SysRole role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.ROLE_NOT_FOUND);
        }

        if (roleDTO.getRoleName() != null) role.setRoleName(roleDTO.getRoleName());
        if (roleDTO.getRoleCode() != null) role.setRoleCode(roleDTO.getRoleCode());
        if (roleDTO.getDescription() != null) role.setDescription(roleDTO.getDescription());
        if (roleDTO.getStatus() != null) role.setStatus(roleDTO.getStatus());
        if (roleDTO.getSort() != null) role.setSort(roleDTO.getSort());

        roleMapper.updateById(role);

        if (roleDTO.getMenuIds() != null) {
            assignMenus(id, roleDTO.getMenuIds());
        }

        log.info("更新角色: roleId={}", id);
        return convertToVO(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        SysRole role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.ROLE_NOT_FOUND);
        }

        if (roleMapper.hasUsersByRoleId(id)) {
            throw new BusinessException(ResultCode.ROLE_HAS_USERS);
        }

        roleMapper.deleteById(id);
        roleMenuMapper.deleteByRoleId(id);
        log.info("删除角色: roleId={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMenus(Long roleId, List<Long> menuIds) {
        roleMenuMapper.deleteByRoleId(roleId);
        if (menuIds != null && !menuIds.isEmpty()) {
            List<SysRoleMenu> roleMenus = new ArrayList<>();
            for (Long menuId : menuIds) {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                roleMenus.add(roleMenu);
            }
            roleMenuMapper.batchInsert(roleMenus);
        }
        log.info("分配菜单权限: roleId={}, menuIds={}", roleId, menuIds);
    }

    @Override
    public List<RoleVO> getRolesByUserId(Long userId) {
        List<SysRole> roles = roleMapper.selectRolesByUserId(userId);
        return roles.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<RoleVO> listAllRoles(String appKey) {
        SysRole query = new SysRole();
        query.setAppKey(appKey);
        List<SysRole> roles = roleMapper.selectList(null);
        return roles.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    private RoleVO convertToVO(SysRole role) {
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);
        return vo;
    }
}
