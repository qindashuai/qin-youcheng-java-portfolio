package com.qinyoucheng.auth.system.service;

import com.qinyoucheng.auth.system.dto.LoginDTO;
import com.qinyoucheng.auth.system.vo.LoginVO;

public interface AuthService {

    LoginVO login(LoginDTO loginDTO, String ip);

    void logout(String accessToken, String appKey);

    LoginVO refreshToken(String refreshToken);

    LoginVO ssoCallback(String ticket, String appKey);

    String generateSsoTicket(Long userId, String appKey);
}
