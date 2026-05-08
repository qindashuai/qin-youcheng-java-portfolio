package com.qinyoucheng.auth.system.controller;

import com.qinyoucheng.auth.common.PageResult;
import com.qinyoucheng.auth.common.Result;
import com.qinyoucheng.auth.system.entity.SysBlackList;
import com.qinyoucheng.auth.system.service.BlackListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/api/v1/blacklist")
public class BlackListController {

    @Resource
    private BlackListService blackListService;

    @GetMapping
    public Result<PageResult<SysBlackList>> list(
            @RequestParam(required = false) Integer targetType,
            @RequestParam(required = false, defaultValue = "default_system") String appKey,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<SysBlackList> result = blackListService.listBlackList(targetType, appKey, pageNum, pageSize);
        return Result.success(result);
    }

    @PostMapping
    public Result<SysBlackList> add(@RequestBody SysBlackList blackList) {
        SysBlackList result = blackListService.addToBlackList(blackList);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        blackListService.removeFromBlackList(id);
        return Result.success();
    }

    @GetMapping("/check/ip")
    public Result<Boolean> checkIp(@RequestParam String ip) {
        boolean blacklisted = blackListService.isIpBlacklisted(ip);
        return Result.success(blacklisted);
    }

    @GetMapping("/check/token")
    public Result<Boolean> checkToken(@RequestParam String token) {
        boolean blacklisted = blackListService.isTokenBlacklisted(token);
        return Result.success(blacklisted);
    }
}
