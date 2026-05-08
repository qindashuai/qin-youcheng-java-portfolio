package com.qinyoucheng.auth.system.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class UserDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String nickname;

    private String email;

    private String phone;

    private String avatar;

    private Integer status;

    private Long deptId;

    @NotBlank(message = "appKey不能为空")
    private String appKey;

    private List<Long> roleIds;
}
