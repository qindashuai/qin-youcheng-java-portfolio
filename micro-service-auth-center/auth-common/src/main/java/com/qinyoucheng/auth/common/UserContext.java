package com.qinyoucheng.auth.common;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class UserContext implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private String username;
    private String appKey;
    private Set<String> permissions;
    private Set<Long> roleIds;
    private String ip;

    public UserContext() {
    }

    public UserContext(Long userId, String username, String appKey) {
        this.userId = userId;
        this.username = username;
        this.appKey = appKey;
    }
}
