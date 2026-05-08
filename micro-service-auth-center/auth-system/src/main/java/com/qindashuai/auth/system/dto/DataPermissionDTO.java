package com.qindashuai.auth.system.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DataPermissionDTO {

    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    @NotNull(message = "权限类型不能为空")
    private Integer permissionType;

    private Long deptId;

    private String customCondition;

    private String appKey;
}
