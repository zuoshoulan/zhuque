-- 添加 API 权限数据
-- 执行时间: 2026-01-11
-- 说明: 为系统主要 API 接口添加权限数据

-- 用户管理 API 权限
INSERT INTO `sys_permission` (`parent_id`, `permission_code`, `permission_name`, `permission_type`, `path`, `method`, `icon`, `sort_order`, `status`, `create_time`, `update_time`) VALUES
(100, 'api:user:page', '用户分页查询API', 3, '/api/users/page', 'GET', NULL, 100, 1, NOW(), NOW()),
(100, 'api:user:create', '创建用户API', 3, '/api/users', 'POST', NULL, 101, 1, NOW(), NOW()),
(100, 'api:user:update', '更新用户API', 3, '/api/users/{id}', 'PUT', NULL, 102, 1, NOW(), NOW()),
(100, 'api:user:delete', '删除用户API', 3, '/api/users/{id}', 'DELETE', NULL, 103, 1, NOW(), NOW()),
(100, 'api:user:reset-password', '重置用户密码API', 3, '/api/users/{id}/reset-password', 'POST', NULL, 104, 1, NOW(), NOW()),
(100, 'api:user:assign-roles', '分配用户角色API', 3, '/api/users/{id}/roles', 'PUT', NULL, 105, 1, NOW(), NOW()),
(100, 'api:user:update-theme', '更新用户主题API', 3, '/api/users/{id}/theme', 'PUT', NULL, 106, 1, NOW(), NOW());

-- 角色管理 API 权限
INSERT INTO `sys_permission` (`parent_id`, `permission_code`, `permission_name`, `permission_type`, `path`, `method`, `icon`, `sort_order`, `status`, `create_time`, `update_time`) VALUES
(3, 'api:role:page', '角色分页查询API', 3, '/api/roles/page', 'GET', NULL, 200, 1, NOW(), NOW()),
(3, 'api:role:list', '角色列表查询API', 3, '/api/roles', 'GET', NULL, 201, 1, NOW(), NOW()),
(3, 'api:role:create', '创建角色API', 3, '/api/roles', 'POST', NULL, 202, 1, NOW(), NOW()),
(3, 'api:role:update', '更新角色API', 3, '/api/roles/{id}', 'PUT', NULL, 203, 1, NOW(), NOW()),
(3, 'api:role:delete', '删除角色API', 3, '/api/roles/{id}', 'DELETE', NULL, 204, 1, NOW(), NOW()),
(3, 'api:role:detail', '角色详情查询API', 3, '/api/roles/{id}', 'GET', NULL, 205, 1, NOW(), NOW()),
(3, 'api:role:assign-permissions', '分配角色权限API', 3, '/api/roles/{id}/permissions', 'PUT', NULL, 206, 1, NOW(), NOW()),
(3, 'api:role:get-permissions', '查询角色权限API', 3, '/api/roles/{id}/permissions', 'GET', NULL, 207, 1, NOW(), NOW()),
(3, 'api:role:update-status', '更新角色状态API', 3, '/api/roles/{id}/status', 'PUT', NULL, 208, 1, NOW(), NOW());

-- 权限管理 API 权限
INSERT INTO `sys_permission` (`parent_id`, `permission_code`, `permission_name`, `permission_type`, `path`, `method`, `icon`, `sort_order`, `status`, `create_time`, `update_time`) VALUES
(4, 'api:permission:tree', '权限树查询API', 3, '/api/permissions/tree', 'GET', NULL, 300, 1, NOW(), NOW()),
(4, 'api:permission:list', '权限列表查询API', 3, '/api/permissions', 'GET', NULL, 301, 1, NOW(), NOW()),
(4, 'api:permission:create', '创建权限API', 3, '/api/permissions', 'POST', NULL, 302, 1, NOW(), NOW()),
(4, 'api:permission:update', '更新权限API', 3, '/api/permissions/{id}', 'PUT', NULL, 303, 1, NOW(), NOW()),
(4, 'api:permission:delete', '删除权限API', 3, '/api/permissions/{id}', 'DELETE', NULL, 304, 1, NOW(), NOW()),
(4, 'api:permission:detail', '权限详情查询API', 3, '/api/permissions/{id}', 'GET', NULL, 305, 1, NOW(), NOW());

-- 认证管理 API 权限
INSERT INTO `sys_permission` (`parent_id`, `permission_code`, `permission_name`, `permission_type`, `path`, `method`, `icon`, `sort_order`, `status`, `create_time`, `update_time`) VALUES
(1, 'api:auth:login', '登录API', 3, '/api/auth/login', 'POST', NULL, 400, 1, NOW(), NOW()),
(1, 'api:auth:logout', '登出API', 3, '/api/auth/logout', 'POST', NULL, 401, 1, NOW(), NOW()),
(1, 'api:auth:info', '获取当前用户信息API', 3, '/api/auth/info', 'GET', NULL, 402, 1, NOW(), NOW());

-- 菜单管理 API 权限
INSERT INTO `sys_permission` (`parent_id`, `permission_code`, `permission_name`, `permission_type`, `path`, `method`, `icon`, `sort_order`, `status`, `create_time`, `update_time`) VALUES
(1, 'api:menu:user', '获取用户菜单API', 3, '/api/menu/user', 'GET', NULL, 500, 1, NOW(), NOW());

-- 广告主管理 API 权限
INSERT INTO `sys_permission` (`parent_id`, `permission_code`, `permission_name`, `permission_type`, `path`, `method`, `icon`, `sort_order`, `status`, `create_time`, `update_time`) VALUES
(1, 'api:advertiser:page', '广告主分页查询API', 3, '/api/advertisers/page', 'GET', NULL, 600, 1, NOW(), NOW()),
(1, 'api:advertiser:create', '创建广告主API', 3, '/api/advertisers', 'POST', NULL, 601, 1, NOW(), NOW()),
(1, 'api:advertiser:update', '更新广告主API', 3, '/api/advertisers/{id}', 'PUT', NULL, 602, 1, NOW(), NOW()),
(1, 'api:advertiser:delete', '删除广告主API', 3, '/api/advertisers/{id}', 'DELETE', NULL, 603, 1, NOW(), NOW());
