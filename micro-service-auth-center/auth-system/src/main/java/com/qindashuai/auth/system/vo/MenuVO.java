package com.qindashuai.auth.system.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MenuVO {

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
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<MenuVO> children;
}
