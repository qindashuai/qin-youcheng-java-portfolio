package com.qinyoucheng.auth.system.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.qinyoucheng.auth.common.BusinessException;
import com.qinyoucheng.auth.common.JwtUtil;
import com.qinyoucheng.auth.common.RedisUtil;
import com.qinyoucheng.auth.common.ResultCode;
import com.qinyoucheng.auth.system.dto.LoginDTO;
import com.qinyoucheng.auth.system.entity.SysUser;
import com.qinyoucheng.auth.system.mapper.SysUserMapper;
import com.qinyoucheng.auth.system.service.AuthService;
import com.qinyoucheng.auth.system.vo.LoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private SysUserMapper userMapper;

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private RedisUtil redisUtil;

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final long SSO_TICKET_EXPIRATION = 300000;

    @Override
    public LoginVO login(LoginDTO loginDTO, String ip) {
        int attempts = redisUtil.getLoginAttemptCount(loginDTO.getUsername());
        if (attempts >= MAX_LOGIN_ATTEMPTS) {
            throw new BusinessException(ResultCode.TOO_MANY_LOGIN_ATTEMPTS);
        }

        SysUser user = userMapper.selectByUsername(loginDTO.getUsername(), loginDTO.getAppKey());
        if (user == null) {
            redisUtil.incrementLoginAttempt(loginDTO.getUsername());
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (user.getStatus() != 1) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        if (!BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) {
            redisUtil.incrementLoginAttempt(loginDTO.getUsername());
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        redisUtil.resetLoginAttempt(loginDTO.getUsername());

        String existingToken = redisUtil.getUserSessionToken(user.getId(), loginDTO.getAppKey());
        if (existingToken != null) {
            long remaining = jwtUtil.getAccessTokenExpiration();
            redisUtil.addToTokenBlacklist(existingToken, remaining);
            redisUtil.removeAccessToken(existingToken);
        }

        List<String> permissions = userMapper.selectPermissionsByUserId(user.getId());
        List<Long> roleIds = userMapper.selectRoleIdsByUserId(user.getId());

        Map<String, Object> claims = new HashMap<>();
        claims.put("permissions", permissions);
        claims.put("roleIds", roleIds);

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), loginDTO.getAppKey(), claims);
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername(), loginDTO.getAppKey());

        redisUtil.storeAccessToken(accessToken, user.getId(), user.getUsername(), loginDTO.getAppKey(), jwtUtil.getAccessTokenExpiration());
        redisUtil.storeRefreshToken(refreshToken, user.getId(), loginDTO.getAppKey(), jwtUtil.getRefreshTokenExpiration());

        log.info("用户登录成功: userId={}, username={}, ip={}", user.getId(), user.getUsername(), ip);

        LoginVO loginVO = new LoginVO();
        loginVO.setAccessToken(accessToken);
        loginVO.setRefreshToken(refreshToken);
        loginVO.setTokenType("Bearer");
        loginVO.setExpiresIn(jwtUtil.getAccessTokenExpiration() / 1000);
        loginVO.setUserId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setNickname(user.getNickname());
        loginVO.setPermissions(permissions);
        loginVO.setRoleIds(roleIds);
        return loginVO;
    }

    @Override
    public void logout(String accessToken, String appKey) {
        if (accessToken == null || accessToken.isEmpty()) {
            return;
        }

        try {
            Long userId = jwtUtil.getUserIdFromToken(accessToken);
            String username = jwtUtil.getUsernameFromToken(accessToken);

            redisUtil.addToTokenBlacklist(accessToken, jwtUtil.getAccessTokenExpiration());
            redisUtil.removeAccessToken(accessToken);
            redisUtil.removeUserSession(userId, appKey);

            log.info("用户登出成功: userId={}, username={}", userId, username);
        } catch (Exception e) {
            log.warn("登出处理异常: {}", e.getMessage());
        }
    }

    @Override
    public LoginVO refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }

        String tokenType = jwtUtil.getTokenTypeFromToken(refreshToken);
        if (!"refresh".equals(tokenType)) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }

        if (!redisUtil.isRefreshTokenExist(refreshToken)) {
            throw new BusinessException(ResultCode.TOKEN_EXPIRED);
        }

        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        String username = jwtUtil.getUsernameFromToken(refreshToken);
        String appKey = jwtUtil.getAppKeyFromToken(refreshToken);

        SysUser user = userMapper.selectById(userId);
        if (user == null || user.getStatus() != 1) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        List<String> permissions = userMapper.selectPermissionsByUserId(userId);
        List<Long> roleIds = userMapper.selectRoleIdsByUserId(userId);

        Map<String, Object> claims = new HashMap<>();
        claims.put("permissions", permissions);
        claims.put("roleIds", roleIds);

        String newAccessToken = jwtUtil.generateAccessToken(userId, username, appKey, claims);
        String newRefreshToken = jwtUtil.generateRefreshToken(userId, username, appKey);

        redisUtil.removeRefreshToken(refreshToken);
        redisUtil.storeAccessToken(newAccessToken, userId, username, appKey, jwtUtil.getAccessTokenExpiration());
        redisUtil.storeRefreshToken(newRefreshToken, userId, appKey, jwtUtil.getRefreshTokenExpiration());

        LoginVO loginVO = new LoginVO();
        loginVO.setAccessToken(newAccessToken);
        loginVO.setRefreshToken(newRefreshToken);
        loginVO.setTokenType("Bearer");
        loginVO.setExpiresIn(jwtUtil.getAccessTokenExpiration() / 1000);
        loginVO.setUserId(userId);
        loginVO.setUsername(username);
        loginVO.setNickname(user.getNickname());
        loginVO.setPermissions(permissions);
        loginVO.setRoleIds(roleIds);
        return loginVO;
    }

    @Override
    public LoginVO ssoCallback(String ticket, String appKey) {
        String ticketInfo = redisUtil.getSsoTicketInfo(ticket);
        if (ticketInfo == null) {
            throw new BusinessException(ResultCode.SSO_TICKET_INVALID);
        }

        redisUtil.removeSsoTicket(ticket);

        String[] parts = ticketInfo.split(":");
        Long userId = Long.parseLong(parts[0]);
        String ticketAppKey = parts[1];

        SysUser user = userMapper.selectById(userId);
        if (user == null || user.getStatus() != 1) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        List<String> permissions = userMapper.selectPermissionsByUserId(userId);
        List<Long> roleIds = userMapper.selectRoleIdsByUserId(userId);

        Map<String, Object> claims = new HashMap<>();
        claims.put("permissions", permissions);
        claims.put("roleIds", roleIds);

        String accessToken = jwtUtil.generateAccessToken(userId, user.getUsername(), ticketAppKey, claims);
        String refreshToken = jwtUtil.generateRefreshToken(userId, user.getUsername(), ticketAppKey);

        redisUtil.storeAccessToken(accessToken, userId, user.getUsername(), ticketAppKey, jwtUtil.getAccessTokenExpiration());
        redisUtil.storeRefreshToken(refreshToken, userId, ticketAppKey, jwtUtil.getRefreshTokenExpiration());

        LoginVO loginVO = new LoginVO();
        loginVO.setAccessToken(accessToken);
        loginVO.setRefreshToken(refreshToken);
        loginVO.setTokenType("Bearer");
        loginVO.setExpiresIn(jwtUtil.getAccessTokenExpiration() / 1000);
        loginVO.setUserId(userId);
        loginVO.setUsername(user.getUsername());
        loginVO.setNickname(user.getNickname());
        loginVO.setPermissions(permissions);
        loginVO.setRoleIds(roleIds);
        return loginVO;
    }

    @Override
    public String generateSsoTicket(Long userId, String appKey) {
        String ticket = IdUtil.fastSimpleUUID();
        redisUtil.storeSsoTicket(ticket, userId, appKey, SSO_TICKET_EXPIRATION);
        return ticket;
    }
}
