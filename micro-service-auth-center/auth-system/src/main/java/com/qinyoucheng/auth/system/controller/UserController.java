package com.qinyoucheng.auth.system.controller;

import com.qinyoucheng.auth.common.PageResult;
import com.qinyoucheng.auth.common.Result;
import com.qinyoucheng.auth.system.dto.UserDTO;
import com.qinyoucheng.auth.system.service.UserService;
import com.qinyoucheng.auth.system.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping
    public Result<PageResult<UserVO>> list(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false, defaultValue = "default_system") String appKey,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<UserVO> result = userService.listUsers(username, status, appKey, pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<UserVO> getById(@PathVariable Long id) {
        UserVO user = userService.getUserById(id);
        return Result.success(user);
    }

    @PostMapping
    public Result<UserVO> create(@Valid @RequestBody UserDTO userDTO) {
        UserVO user = userService.createUser(userDTO);
        return Result.success(user);
    }

    @PutMapping("/{id}")
    public Result<UserVO> update(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        UserVO user = userService.updateUser(id, userDTO);
        return Result.success(user);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestParam String newPassword) {
        userService.resetPassword(id, newPassword);
        return Result.success();
    }

    @PutMapping("/{id}/roles")
    public Result<Void> assignRoles(@PathVariable Long id, @RequestBody List<Long> roleIds) {
        userService.assignRoles(id, roleIds);
        return Result.success();
    }
}
