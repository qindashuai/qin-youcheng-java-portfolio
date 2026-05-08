package com.qinyoucheng.auth.system.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserVO {

    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    private Integer status;
    private Long deptId;
    private String appKey;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<Long> roleIds;
}
