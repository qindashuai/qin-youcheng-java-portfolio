package com.qindashuai.auth.system.service.impl;

import com.qindashuai.auth.common.BusinessException;
import com.qindashuai.auth.common.ResultCode;
import com.qindashuai.auth.system.entity.SysMenu;
import com.qindashuai.auth.system.mapper.SysMenuMapper;
import com.qindashuai.auth.system.service.MenuService;
import com.qindashuai.auth.system.vo.MenuVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MenuServiceImpl implements MenuService {

    @Resource
    private SysMenuMapper menuMapper;

    @Override
    public List<MenuVO> getMenuTree(String appKey) {
        List<SysMenu> allMenus = menuMapper.selectMenuTree(appKey);
        return buildMenuTree(allMenus, 0L);
    }

    @Override
    public MenuVO getMenuById(Long id) {
        SysMenu menu = menuMapper.selectById(id);
        if (menu == null) {
            throw new BusinessException(ResultCode.MENU_NOT_FOUND);
        }
        return convertToVO(menu);
    }

    @Override
    public MenuVO createMenu(SysMenu menu) {
        menu.setStatus(menu.getStatus() != null ? menu.getStatus() : 1);
        menu.setVisible(menu.getVisible() != null ? menu.getVisible() : 1);
        menuMapper.insert(menu);
        log.info("创建菜单: menuId={}, menuName={}", menu.getId(), menu.getMenuName());
        return convertToVO(menu);
    }

    @Override
    public MenuVO updateMenu(Long id, SysMenu menu) {
        SysMenu existing = menuMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.MENU_NOT_FOUND);
        }
        menu.setId(id);
        menuMapper.updateById(menu);
        log.info("更新菜单: menuId={}", id);
        return convertToVO(menu);
    }

    @Override
    public void deleteMenu(Long id) {
        SysMenu menu = menuMapper.selectById(id);
        if (menu == null) {
            throw new BusinessException(ResultCode.MENU_NOT_FOUND);
        }
        menuMapper.deleteById(id);
        log.info("删除菜单: menuId={}", id);
    }

    @Override
    public List<MenuVO> getMenusByRoleId(Long roleId) {
        List<SysMenu> menus = menuMapper.selectMenusByRoleId(roleId);
        return menus.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<MenuVO> getMenusByUserId(Long userId) {
        List<SysMenu> menus = menuMapper.selectMenusByUserId(userId);
        return buildMenuTree(menus, 0L);
    }

    private List<MenuVO> buildMenuTree(List<SysMenu> menus, Long parentId) {
        List<MenuVO> tree = new ArrayList<>();
        for (SysMenu menu : menus) {
            if (parentId.equals(menu.getParentId())) {
                MenuVO vo = convertToVO(menu);
                vo.setChildren(buildMenuTree(menus, menu.getId()));
                tree.add(vo);
            }
        }
        return tree;
    }

    private MenuVO convertToVO(SysMenu menu) {
        MenuVO vo = new MenuVO();
        BeanUtils.copyProperties(menu, vo);
        return vo;
    }
}
