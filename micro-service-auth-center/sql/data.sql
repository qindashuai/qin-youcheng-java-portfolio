USE auth_center;

-- ----------------------------
-- 初始化菜单数据
-- ----------------------------
INSERT INTO sys_menu (id, parent_id, menu_name, menu_code, path, component, icon, menu_type, permission, sort, visible, status, app_key) VALUES
(1, 0, '系统管理', 'system', '/system', NULL, 'setting', 1, NULL, 1, 1, 1, 'default_system'),
(2, 1, '用户管理', 'user', '/system/user', 'system/user/index', 'user', 2, 'system:user:list', 1, 1, 1, 'default_system'),
(3, 1, '角色管理', 'role', '/system/role', 'system/role/index', 'peoples', 2, 'system:role:list', 2, 1, 1, 'default_system'),
(4, 1, '菜单管理', 'menu', '/system/menu', 'system/menu/index', 'tree-table', 2, 'system:menu:list', 3, 1, 1, 'default_system'),
(5, 1, '数据权限', 'data-permission', '/system/data-permission', 'system/data-permission/index', 'chart', 2, 'system:data-permission:list', 4, 1, 1, 'default_system'),
(6, 1, '黑名单管理', 'blacklist', '/system/blacklist', 'system/blacklist/index', 'lock', 2, 'system:blacklist:list', 5, 1, 1, 'default_system'),
(7, 1, '操作日志', 'operation-log', '/system/log', 'system/log/index', 'log', 2, 'system:log:list', 6, 1, 1, 'default_system'),
(8, 2, '用户新增', NULL, NULL, NULL, NULL, 3, 'system:user:add', 1, 1, 1, 'default_system'),
(9, 2, '用户修改', NULL, NULL, NULL, NULL, 3, 'system:user:edit', 2, 1, 1, 'default_system'),
(10, 2, '用户删除', NULL, NULL, NULL, NULL, 3, 'system:user:delete', 3, 1, 1, 'default_system'),
(11, 2, '重置密码', NULL, NULL, NULL, NULL, 3, 'system:user:resetPwd', 4, 1, 1, 'default_system'),
(12, 3, '角色新增', NULL, NULL, NULL, NULL, 3, 'system:role:add', 1, 1, 1, 'default_system'),
(13, 3, '角色修改', NULL, NULL, NULL, NULL, 3, 'system:role:edit', 2, 1, 1, 'default_system'),
(14, 3, '角色删除', NULL, NULL, NULL, NULL, 3, 'system:role:delete', 3, 1, 1, 'default_system'),
(15, 4, '菜单新增', NULL, NULL, NULL, NULL, 3, 'system:menu:add', 1, 1, 1, 'default_system'),
(16, 4, '菜单修改', NULL, NULL, NULL, NULL, 3, 'system:menu:edit', 2, 1, 1, 'default_system'),
(17, 4, '菜单删除', NULL, NULL, NULL, NULL, 3, 'system:menu:delete', 3, 1, 1, 'default_system'),
(18, 5, '权限新增', NULL, NULL, NULL, NULL, 3, 'system:data-permission:add', 1, 1, 1, 'default_system'),
(19, 5, '权限修改', NULL, NULL, NULL, NULL, 3, 'system:data-permission:edit', 2, 1, 1, 'default_system'),
(20, 5, '权限删除', NULL, NULL, NULL, NULL, 3, 'system:data-permission:delete', 3, 1, 1, 'default_system'),
(21, 6, '黑名单新增', NULL, NULL, NULL, NULL, 3, 'system:blacklist:add', 1, 1, 1, 'default_system'),
(22, 6, '黑名单删除', NULL, NULL, NULL, NULL, 3, 'system:blacklist:delete', 2, 1, 1, 'default_system');

-- ----------------------------
-- 初始化角色数据
-- ----------------------------
INSERT INTO sys_role (id, role_name, role_code, description, status, sort, app_key) VALUES
(1, '超级管理员', 'super_admin', '拥有系统所有权限', 1, 1, 'default_system'),
(2, '系统管理员', 'admin', '拥有系统管理权限', 1, 2, 'default_system'),
(3, '普通用户', 'user', '基础操作权限', 1, 3, 'default_system');

-- ----------------------------
-- 初始化用户数据 (密码均为 admin123)
-- ----------------------------
INSERT INTO sys_user (id, username, password, nickname, email, phone, status, dept_id, app_key) VALUES
(1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '超级管理员', 'admin@qinyoucheng.com', '13800138000', 1, 1, 'default_system'),
(2, 'system_admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '系统管理员', 'sysadmin@qinyoucheng.com', '13800138001', 1, 1, 'default_system'),
(3, 'normal_user', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '普通用户', 'user@qinyoucheng.com', '13800138002', 1, 2, 'default_system');

-- ----------------------------
-- 初始化用户角色关联
-- ----------------------------
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),
(2, 2),
(3, 3);

-- ----------------------------
-- 初始化角色菜单关联 (超级管理员拥有所有菜单)
-- ----------------------------
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7),
(1, 8), (1, 9), (1, 10), (1, 11), (1, 12), (1, 13), (1, 14),
(1, 15), (1, 16), (1, 17), (1, 18), (1, 19), (1, 20), (1, 21), (1, 22),
(2, 1), (2, 2), (2, 3), (2, 4), (2, 7),
(2, 8), (2, 9), (2, 10), (2, 12), (2, 13), (2, 15), (2, 16),
(3, 1), (3, 2), (3, 7);

-- ----------------------------
-- 初始化数据权限
-- ----------------------------
INSERT INTO sys_data_permission (role_id, permission_type, dept_id, app_key) VALUES
(1, 1, NULL, 'default_system'),
(2, 2, 1, 'default_system'),
(3, 3, 2, 'default_system');

-- ----------------------------
-- 初始化白名单
-- ----------------------------
INSERT INTO sys_white_list (path, description, app_key) VALUES
('/api/v1/auth/login', '登录接口', 'default_system'),
('/api/v1/auth/sso/callback', 'SSO回调接口', 'default_system'),
('/api/v1/auth/sso/ticket', 'SSO Ticket接口', 'default_system'),
('/api/v1/auth/refresh', 'Token刷新接口', 'default_system');
