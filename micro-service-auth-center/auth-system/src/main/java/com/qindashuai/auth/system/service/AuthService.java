package com.qindashuai.auth.system.service;

import com.qindashuai.auth.system.dto.LoginDTO;
import com.qindashuai.auth.system.vo.LoginVO;

public interface AuthService {

    LoginVO login(LoginDTO loginDTO, String ip);

    void logout(String accessToken, String appKey);

    LoginVO refreshToken(String refreshToken);

    LoginVO ssoCallback(String ticket, String appKey);

    String generateSsoTicket(Long userId, String appKey);
}
