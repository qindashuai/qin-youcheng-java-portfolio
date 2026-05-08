package com.qinyoucheng.auth.system.controller;

import com.qinyoucheng.auth.common.PageResult;
import com.qinyoucheng.auth.common.Result;
import com.qinyoucheng.auth.system.dto.DataPermissionDTO;
import com.qinyoucheng.auth.system.entity.SysDataPermission;
import com.qinyoucheng.auth.system.service.DataPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/data-permissions")
public class DataPermissionController {

    @Resource
    private DataPermissionService dataPermissionService;

    @GetMapping
    public Result<PageResult<SysDataPermission>> list(
            @RequestParam(required = false) Long roleId,
            @RequestParam(required = false, defaultValue = "default_system") String appKey,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<SysDataPermission> result = dataPermissionService.listDataPermissions(roleId, appKey, pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<SysDataPermission> getById(@PathVariable Long id) {
        SysDataPermission permission = dataPermissionService.getDataPermissionById(id);
        return Result.success(permission);
    }

    @PostMapping
    public Result<SysDataPermission> create(@Valid @RequestBody DataPermissionDTO dto) {
        SysDataPermission permission = dataPermissionService.createDataPermission(dto);
        return Result.success(permission);
    }

    @PutMapping("/{id}")
    public Result<SysDataPermission> update(@PathVariable Long id, @Valid @RequestBody DataPermissionDTO dto) {
        SysDataPermission permission = dataPermissionService.updateDataPermission(id, dto);
        return Result.success(permission);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dataPermissionService.deleteDataPermission(id);
        return Result.success();
    }

    @GetMapping("/role/{roleId}")
    public Result<SysDataPermission> getByRoleId(
            @PathVariable Long roleId,
            @RequestParam(required = false, defaultValue = "default_system") String appKey) {
        SysDataPermission permission = dataPermissionService.getDataPermissionByRoleId(roleId, appKey);
        return Result.success(permission);
    }
}
