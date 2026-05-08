package com.qinyoucheng.auth.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_menu")
public class SysMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long parentId;

    private String menuName;

    private String menuCode;

    private String path;

    private String component;

    private String icon;

    private Integer menuType;

    private String permission;

    private Integer sort;

    private Integer visible;

    private Integer status;

    private String appKey;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String parentName;

    @TableField(exist = false)
    private java.util.List<SysMenu> children;
}
