package com.qinyoucheng.auth.system.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class RoleDTO {

    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    @NotBlank(message = "角色编码不能为空")
    private String roleCode;

    private String description;

    private Integer status;

    private Integer sort;

    @NotBlank(message = "appKey不能为空")
    private String appKey;

    private List<Long> menuIds;
}
