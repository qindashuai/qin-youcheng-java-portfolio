package com.qinyoucheng.auth.system.service;

import com.qinyoucheng.auth.system.entity.SysMenu;
import com.qinyoucheng.auth.system.vo.MenuVO;

import java.util.List;

public interface MenuService {

    List<MenuVO> getMenuTree(String appKey);

    MenuVO getMenuById(Long id);

    MenuVO createMenu(SysMenu menu);

    MenuVO updateMenu(Long id, SysMenu menu);

    void deleteMenu(Long id);

    List<MenuVO> getMenusByRoleId(Long roleId);

    List<MenuVO> getMenusByUserId(Long userId);
}
