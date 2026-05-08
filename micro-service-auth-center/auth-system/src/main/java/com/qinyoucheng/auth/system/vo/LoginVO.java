package com.qinyoucheng.auth.system.vo;

import lombok.Data;

import java.util.List;

@Data
public class LoginVO {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private Long userId;
    private String username;
    private String nickname;
    private List<String> permissions;
    private List<Long> roleIds;
}
