package com.qinyoucheng.auth.system.controller;

import com.qinyoucheng.auth.common.PageResult;
import com.qinyoucheng.auth.common.Result;
import com.qinyoucheng.auth.system.dto.RoleDTO;
import com.qinyoucheng.auth.system.service.RoleService;
import com.qinyoucheng.auth.system.vo.RoleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    @Resource
    private RoleService roleService;

    @GetMapping
    public Result<PageResult<RoleVO>> list(
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false, defaultValue = "default_system") String appKey,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<RoleVO> result = roleService.listRoles(roleName, status, appKey, pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/all")
    public Result<List<RoleVO>> listAll(@RequestParam(required = false, defaultValue = "default_system") String appKey) {
        List<RoleVO> roles = roleService.listAllRoles(appKey);
        return Result.success(roles);
    }

    @GetMapping("/{id}")
    public Result<RoleVO> getById(@PathVariable Long id) {
        RoleVO role = roleService.getRoleById(id);
        return Result.success(role);
    }

    @PostMapping
    public Result<RoleVO> create(@Valid @RequestBody RoleDTO roleDTO) {
        RoleVO role = roleService.createRole(roleDTO);
        return Result.success(role);
    }

    @PutMapping("/{id}")
    public Result<RoleVO> update(@PathVariable Long id, @Valid @RequestBody RoleDTO roleDTO) {
        RoleVO role = roleService.updateRole(id, roleDTO);
        return Result.success(role);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success();
    }

    @PutMapping("/{id}/menus")
    public Result<Void> assignMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        roleService.assignMenus(id, menuIds);
        return Result.success();
    }

    @GetMapping("/user/{userId}")
    public Result<List<RoleVO>> getRolesByUserId(@PathVariable Long userId) {
        List<RoleVO> roles = roleService.getRolesByUserId(userId);
        return Result.success(roles);
    }
}
