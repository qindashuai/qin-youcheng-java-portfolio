package com.qinyoucheng.auth.system.controller;

import com.qinyoucheng.auth.common.Result;
import com.qinyoucheng.auth.system.entity.SysMenu;
import com.qinyoucheng.auth.system.service.MenuService;
import com.qinyoucheng.auth.system.vo.MenuVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/menus")
public class MenuController {

    @Resource
    private MenuService menuService;

    @GetMapping("/tree")
    public Result<List<MenuVO>> getMenuTree(@RequestParam(required = false, defaultValue = "default_system") String appKey) {
        List<MenuVO> tree = menuService.getMenuTree(appKey);
        return Result.success(tree);
    }

    @GetMapping("/{id}")
    public Result<MenuVO> getById(@PathVariable Long id) {
        MenuVO menu = menuService.getMenuById(id);
        return Result.success(menu);
    }

    @PostMapping
    public Result<MenuVO> create(@RequestBody SysMenu menu) {
        MenuVO created = menuService.createMenu(menu);
        return Result.success(created);
    }

    @PutMapping("/{id}")
    public Result<MenuVO> update(@PathVariable Long id, @RequestBody SysMenu menu) {
        MenuVO updated = menuService.updateMenu(id, menu);
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return Result.success();
    }

    @GetMapping("/role/{roleId}")
    public Result<List<MenuVO>> getMenusByRoleId(@PathVariable Long roleId) {
        List<MenuVO> menus = menuService.getMenusByRoleId(roleId);
        return Result.success(menus);
    }

    @GetMapping("/user/{userId}")
    public Result<List<MenuVO>> getMenusByUserId(@PathVariable Long userId) {
        List<MenuVO> menus = menuService.getMenusByUserId(userId);
        return Result.success(menus);
    }
}
