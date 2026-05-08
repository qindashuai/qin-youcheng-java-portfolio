CREATE DATABASE IF NOT EXISTS auth_center DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE auth_center;

-- ----------------------------
-- 系统用户表
-- ----------------------------
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(64) NOT NULL COMMENT '用户名',
    password VARCHAR(128) NOT NULL COMMENT '密码(BCrypt加密)',
    nickname VARCHAR(64) DEFAULT NULL COMMENT '昵称',
    email VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    avatar VARCHAR(256) DEFAULT NULL COMMENT '头像URL',
    status INT NOT NULL DEFAULT 1 COMMENT '状态: 1-正常 0-禁用',
    dept_id BIGINT DEFAULT NULL COMMENT '部门ID',
    app_key VARCHAR(64) NOT NULL DEFAULT 'default_system' COMMENT '所属系统标识',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username_appkey (username, app_key, deleted),
    KEY idx_status (status),
    KEY idx_dept_id (dept_id),
    KEY idx_app_key (app_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- ----------------------------
-- 系统角色表
-- ----------------------------
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    role_name VARCHAR(64) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(64) NOT NULL COMMENT '角色编码',
    description VARCHAR(256) DEFAULT NULL COMMENT '角色描述',
    status INT NOT NULL DEFAULT 1 COMMENT '状态: 1-正常 0-禁用',
    sort INT NOT NULL DEFAULT 0 COMMENT '排序',
    app_key VARCHAR(64) NOT NULL DEFAULT 'default_system' COMMENT '所属系统标识',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_rolecode_appkey (role_code, app_key, deleted),
    KEY idx_app_key (app_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统角色表';

-- ----------------------------
-- 系统菜单表
-- ----------------------------
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父菜单ID',
    menu_name VARCHAR(64) NOT NULL COMMENT '菜单名称',
    menu_code VARCHAR(64) DEFAULT NULL COMMENT '菜单编码',
    path VARCHAR(256) DEFAULT NULL COMMENT '路由路径',
    component VARCHAR(256) DEFAULT NULL COMMENT '组件路径',
    icon VARCHAR(128) DEFAULT NULL COMMENT '菜单图标',
    menu_type INT NOT NULL DEFAULT 1 COMMENT '菜单类型: 1-目录 2-菜单 3-按钮',
    permission VARCHAR(128) DEFAULT NULL COMMENT '权限标识',
    sort INT NOT NULL DEFAULT 0 COMMENT '排序',
    visible INT NOT NULL DEFAULT 1 COMMENT '是否可见: 1-可见 0-隐藏',
    status INT NOT NULL DEFAULT 1 COMMENT '状态: 1-正常 0-禁用',
    app_key VARCHAR(64) NOT NULL DEFAULT 'default_system' COMMENT '所属系统标识',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除 1-已删除',
    PRIMARY KEY (id),
    KEY idx_parent_id (parent_id),
    KEY idx_app_key (app_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统菜单表';

-- ----------------------------
-- 用户角色关联表
-- ----------------------------
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- ----------------------------
-- 角色菜单关联表
-- ----------------------------
DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_role_id (role_id),
    KEY idx_menu_id (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联表';

-- ----------------------------
-- 数据权限表
-- ----------------------------
DROP TABLE IF EXISTS sys_data_permission;
CREATE TABLE sys_data_permission (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_type INT NOT NULL DEFAULT 1 COMMENT '权限类型: 1-全部数据 2-本部门 3-本部门及下级 4-自定义',
    dept_id BIGINT DEFAULT NULL COMMENT '部门ID(permission_type=4时使用)',
    custom_condition VARCHAR(512) DEFAULT NULL COMMENT '自定义条件(permission_type=4时使用)',
    app_key VARCHAR(64) NOT NULL DEFAULT 'default_system' COMMENT '所属系统标识',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除 1-已删除',
    PRIMARY KEY (id),
    KEY idx_role_id (role_id),
    KEY idx_app_key (app_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据权限表';

-- ----------------------------
-- 黑名单表
-- ----------------------------
DROP TABLE IF EXISTS sys_blacklist;
CREATE TABLE sys_blacklist (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    target_type INT NOT NULL DEFAULT 1 COMMENT '目标类型: 1-IP 2-Token 3-用户',
    target_value VARCHAR(256) NOT NULL COMMENT '目标值',
    reason VARCHAR(512) DEFAULT NULL COMMENT '加入黑名单原因',
    expire_time BIGINT DEFAULT NULL COMMENT '过期时间(时间戳, null表示永久)',
    app_key VARCHAR(64) NOT NULL DEFAULT 'default_system' COMMENT '所属系统标识',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除 1-已删除',
    PRIMARY KEY (id),
    KEY idx_target (target_type, target_value),
    KEY idx_app_key (app_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='黑名单表';

-- ----------------------------
-- 白名单表
-- ----------------------------
DROP TABLE IF EXISTS sys_white_list;
CREATE TABLE sys_white_list (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    path VARCHAR(256) NOT NULL COMMENT '白名单路径',
    description VARCHAR(256) DEFAULT NULL COMMENT '描述',
    app_key VARCHAR(64) NOT NULL DEFAULT 'default_system' COMMENT '所属系统标识',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除 1-已删除',
    PRIMARY KEY (id),
    KEY idx_app_key (app_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='白名单表';

-- ----------------------------
-- 操作日志表
-- ----------------------------
DROP TABLE IF EXISTS sys_operation_log;
CREATE TABLE sys_operation_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    trace_id VARCHAR(64) DEFAULT NULL COMMENT '链路追踪ID',
    user_id BIGINT DEFAULT NULL COMMENT '操作用户ID',
    username VARCHAR(64) DEFAULT NULL COMMENT '操作用户名',
    operation_type VARCHAR(32) DEFAULT NULL COMMENT '操作类型',
    operation_desc VARCHAR(256) DEFAULT NULL COMMENT '操作描述',
    request_method VARCHAR(10) DEFAULT NULL COMMENT '请求方法',
    request_url VARCHAR(256) DEFAULT NULL COMMENT '请求URL',
    request_params TEXT DEFAULT NULL COMMENT '请求参数',
    response_result TEXT DEFAULT NULL COMMENT '响应结果',
    ip VARCHAR(64) DEFAULT NULL COMMENT '操作IP',
    duration BIGINT DEFAULT NULL COMMENT '耗时(毫秒)',
    status INT DEFAULT 1 COMMENT '操作状态: 1-成功 0-失败',
    error_msg TEXT DEFAULT NULL COMMENT '错误信息',
    app_key VARCHAR(64) NOT NULL DEFAULT 'default_system' COMMENT '所属系统标识',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_operation_type (operation_type),
    KEY idx_create_time (create_time),
    KEY idx_app_key (app_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';
