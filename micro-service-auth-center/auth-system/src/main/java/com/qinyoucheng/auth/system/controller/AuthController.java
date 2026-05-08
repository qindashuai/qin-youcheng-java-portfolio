package com.qinyoucheng.auth.system.controller;

import com.qinyoucheng.auth.common.Result;
import com.qinyoucheng.auth.system.dto.LoginDTO;
import com.qinyoucheng.auth.system.service.AuthService;
import com.qinyoucheng.auth.system.vo.LoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Resource
    private AuthService authService;

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        String ip = getClientIp(request);
        LoginVO loginVO = authService.login(loginDTO, ip);
        return Result.success(loginVO);
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String accessToken = extractToken(request);
        String appKey = request.getHeader("X-App-Key");
        authService.logout(accessToken, appKey);
        return Result.success();
    }

    @PostMapping("/refresh")
    public Result<LoginVO> refreshToken(@RequestParam String refreshToken) {
        LoginVO loginVO = authService.refreshToken(refreshToken);
        return Result.success(loginVO);
    }

    @GetMapping("/sso/ticket")
    public Result<String> generateSsoTicket(@RequestParam Long userId, @RequestParam String appKey) {
        String ticket = authService.generateSsoTicket(userId, appKey);
        return Result.success(ticket);
    }

    @PostMapping("/sso/callback")
    public Result<LoginVO> ssoCallback(@RequestParam String ticket, @RequestParam String appKey) {
        LoginVO loginVO = authService.ssoCallback(ticket, appKey);
        return Result.success(loginVO);
    }

    private String extractToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return null;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            int index = ip.indexOf(',');
            return index != -1 ? ip.substring(0, index) : ip;
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }
}
