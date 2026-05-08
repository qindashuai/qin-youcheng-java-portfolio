package com.qinyoucheng.auth.system.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RoleVO {

    private Long id;
    private String roleName;
    private String roleCode;
    private String description;
    private Integer status;
    private Integer sort;
    private String appKey;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<Long> menuIds;
}
