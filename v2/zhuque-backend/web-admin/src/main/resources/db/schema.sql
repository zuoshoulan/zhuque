-- ================================
-- RBAC权限管理相关表
-- ================================

-- 清空并重建表（仅用于开发环境初始化）
-- 注意：生产环境请勿执行 DROP TABLE
DROP TABLE IF EXISTS `sys_role_permission`;
DROP TABLE IF EXISTS `sys_user_role`;
DROP TABLE IF EXISTS `sys_menu`;
DROP TABLE IF EXISTS `sys_permission`;
DROP TABLE IF EXISTS `sys_role`;
DROP TABLE IF EXISTS `sys_user`;

-- 用户表
CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `force_change_password` TINYINT DEFAULT 0 COMMENT '是否强制修改密码：0-否，1-是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_email` (`email`),
    KEY `idx_phone` (`phone`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS `sys_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码（唯一标识）',
    `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '角色描述',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

-- 权限表（统一管理路由、按钮、接口权限）
CREATE TABLE IF NOT EXISTS `sys_permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父权限ID（0表示根权限）',
    `permission_code` VARCHAR(100) NOT NULL COMMENT '权限编码（唯一标识）',
    `permission_name` VARCHAR(50) NOT NULL COMMENT '权限名称',
    `permission_type` TINYINT NOT NULL COMMENT '权限类型：1-路由 2-按钮 3-接口',
    `path` VARCHAR(200) DEFAULT NULL COMMENT '路由路径/接口路径',
    `method` VARCHAR(10) DEFAULT NULL COMMENT 'HTTP方法：GET/POST/PUT/DELETE等',
    `icon` VARCHAR(50) DEFAULT NULL COMMENT '图标（用于菜单展示）',
    `sort_order` INT DEFAULT 0 COMMENT '排序号',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_permission_code` (`permission_code`),
    KEY `idx_permission_type` (`permission_type`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统权限表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS `sys_user_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS `sys_role_permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT NOT NULL COMMENT '权限ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 菜单表（前端菜单展示，从权限表派生）
CREATE TABLE IF NOT EXISTS `sys_menu` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父菜单ID（0表示根菜单）',
    `menu_name` VARCHAR(50) NOT NULL COMMENT '菜单名称',
    `menu_type` TINYINT NOT NULL COMMENT '菜单类型：1-目录 2-菜单 3-按钮',
    `icon` VARCHAR(50) DEFAULT NULL COMMENT '菜单图标',
    `path` VARCHAR(200) DEFAULT NULL COMMENT '路由路径',
    `component` VARCHAR(200) DEFAULT NULL COMMENT '组件路径',
    `permission_code` VARCHAR(100) DEFAULT NULL COMMENT '权限编码（关联sys_permission）',
    `sort_order` INT DEFAULT 0 COMMENT '排序号',
    `visible` TINYINT NOT NULL DEFAULT 1 COMMENT '是否显示：0-隐藏，1-显示',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_permission_code` (`permission_code`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统菜单表';

-- ================================
-- 初始化数据
-- ================================

-- 初始化默认用户（密码：Admin123）
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `real_name`, `email`, `phone`, `status`)
VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '管理员', '系统管理员', 'admin@zhuque.com', '13800000001', 1)
ON DUPLICATE KEY UPDATE `id` = `id`;

-- 初始化角色
INSERT INTO `sys_role` (`id`, `role_code`, `role_name`, `description`, `status`)
VALUES
(1, 'SUPER_ADMIN', '超级管理员', '拥有系统所有权限', 1),
(2, 'ADMIN', '管理员', '拥有大部分业务权限', 1),
(3, 'OPERATOR', '运营人员', '拥有基础运营权限', 1),
(4, 'ADVERTISER', '广告主', '广告主角色，只能查看自己的数据', 1)
ON DUPLICATE KEY UPDATE `id` = `id`;

-- 初始化权限（以"广告主管理"为例）
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_code`, `permission_name`, `permission_type`, `path`, `method`, `sort_order`, `status`) VALUES
-- 广告主管理模块（路由权限）
(1, 0, 'advertiser', '广告主管理', 1, '/advertisers', NULL, 10, 1),
-- 广告主管理子权限（按钮权限）
(2, 1, 'advertiser:create', '新增广告主', 2, NULL, 'POST', 1, 1),
(3, 1, 'advertiser:update', '编辑广告主', 2, NULL, 'PUT', 2, 1),
(4, 1, 'advertiser:delete', '删除广告主', 2, NULL, 'DELETE', 3, 1),
(5, 1, 'advertiser:query', '查看广告主', 2, NULL, 'GET', 4, 1),
(6, 1, 'advertiser:audit', '审核广告主', 2, NULL, 'PUT', 5, 1),
-- 广告主管理接口权限
(7, 1, 'api:advertiser:list', '广告主列表接口', 3, '/api/advertisers', 'GET', 10, 1),
(8, 1, 'api:advertiser:create', '创建广告主接口', 3, '/api/advertisers', 'POST', 11, 1),
(9, 1, 'api:advertiser:update', '更新广告主接口', 3, '/api/advertisers/*', 'PUT', 12, 1),
(10, 1, 'api:advertiser:delete', '删除广告主接口', 3, '/api/advertisers/*', 'DELETE', 13, 1),
(11, 1, 'api:advertiser:detail', '广告主详情接口', 3, '/api/advertisers/*', 'GET', 14, 1),

-- 系统管理模块
(100, 0, 'system', '系统管理', 1, '/system', NULL, 100, 1),
(101, 100, 'system:user', '用户管理', 1, '/system/users', NULL, 1, 1),
(102, 100, 'system:role', '角色管理', 1, '/system/roles', NULL, 2, 1),
(103, 100, 'system:permission', '权限管理', 1, '/system/permissions', NULL, 3, 1),
(104, 100, 'system:menu', '菜单管理', 1, '/system/menus', NULL, 4, 1)
ON DUPLICATE KEY UPDATE `id` = `id`;

-- 初始化菜单
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `icon`, `path`, `component`, `permission_code`, `sort_order`, `visible`, `status`) VALUES
-- 广告主管理菜单
(1, 0, '广告主管理', 2, 'User', '/advertisers', 'views/advertiser/list.vue', 'advertiser', 10, 1, 1),
-- 系统管理菜单
(100, 0, '系统管理', 1, 'Setting', '/system', NULL, 'system', 100, 1, 1),
(101, 100, '用户管理', 2, 'UserFilled', '/system/users', 'views/system/user/list.vue', 'system:user', 1, 1, 1),
(102, 100, '角色管理', 2, 'Avatar', '/system/roles', 'views/system/role/list.vue', 'system:role', 2, 1, 1),
(103, 100, '权限管理', 2, 'Key', '/system/permissions', 'views/system/permission/list.vue', 'system:permission', 3, 1, 1),
(104, 100, '菜单管理', 2, 'Menu', '/system/menus', 'views/system/menu/list.vue', 'system:menu', 4, 1, 1)
ON DUPLICATE KEY UPDATE `id` = `id`;

-- 为超级管理员分配所有权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT 1, `id` FROM `sys_permission`
ON DUPLICATE KEY UPDATE `role_id` = `role_id`;

-- 为管理员分配普通用户权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5),  -- 广告主基础权限
(2, 7), (2, 8), (2, 9), (2, 10), (2, 11)  -- 广告主接口权限
ON DUPLICATE KEY UPDATE `role_id` = `role_id`;

-- 为admin用户分配超级管理员角色
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, 1)
ON DUPLICATE KEY UPDATE `user_id` = `user_id`;
