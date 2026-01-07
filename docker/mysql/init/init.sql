-- 朱雀广告平台 - MySQL初始化脚本（开发环境）

-- 创建数据库
CREATE DATABASE IF NOT EXISTS zhuque_dev DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE zhuque_dev;

-- ========================================
-- 认证授权相关表
-- ========================================

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
  password VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
  real_name VARCHAR(50) COMMENT '真实姓名',
  email VARCHAR(100) COMMENT '邮箱',
  phone VARCHAR(20) COMMENT '手机号',
  avatar VARCHAR(255) COMMENT '头像URL',
  status TINYINT DEFAULT 1 COMMENT '状态：1-正常 0-禁用',
  user_type TINYINT DEFAULT 1 COMMENT '用户类型：1-管理员 2-广告主 3-运营',
  force_change_password TINYINT DEFAULT 0 COMMENT '是否强制修改密码：1-是 0-否',
  last_change_password_time DATETIME COMMENT '上次修改密码时间',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_username (username),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
  role_name VARCHAR(50) UNIQUE NOT NULL COMMENT '角色名称',
  role_code VARCHAR(50) UNIQUE NOT NULL COMMENT '角色编码',
  description VARCHAR(200) COMMENT '角色描述',
  status TINYINT DEFAULT 1 COMMENT '状态：1-正常 0-禁用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

-- 权限表
CREATE TABLE IF NOT EXISTS sys_permission (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
  permission_name VARCHAR(50) NOT NULL COMMENT '权限名称',
  permission_code VARCHAR(100) UNIQUE NOT NULL COMMENT '权限编码',
  permission_type TINYINT NOT NULL COMMENT '权限类型：1-菜单 2-按钮 3-API',
  parent_id BIGINT DEFAULT 0 COMMENT '父权限ID',
  path VARCHAR(200) COMMENT '路由路径',
  component VARCHAR(200) COMMENT '组件路径',
  icon VARCHAR(100) COMMENT '图标',
  sort_order INT DEFAULT 0 COMMENT '排序号',
  status TINYINT DEFAULT 1 COMMENT '状态：1-正常 0-禁用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_permission_code (permission_code),
  INDEX idx_permission_type (permission_type),
  INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统权限表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL COMMENT '用户ID',
  role_id BIGINT NOT NULL COMMENT '角色ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_user_role (user_id, role_id),
  INDEX idx_user_id (user_id),
  INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_id BIGINT NOT NULL COMMENT '角色ID',
  permission_id BIGINT NOT NULL COMMENT '权限ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_role_permission (role_id, permission_id),
  INDEX idx_role_id (role_id),
  INDEX idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 登录日志表
CREATE TABLE IF NOT EXISTS sys_login_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
  user_id BIGINT COMMENT '用户ID',
  username VARCHAR(50) COMMENT '用户名',
  login_ip VARCHAR(50) COMMENT '登录IP',
  login_location VARCHAR(100) COMMENT '登录地点',
  browser VARCHAR(50) COMMENT '浏览器',
  os VARCHAR(50) COMMENT '操作系统',
  status TINYINT COMMENT '登录状态：1-成功 0-失败',
  msg VARCHAR(255) COMMENT '提示信息',
  login_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  INDEX idx_user_id (user_id),
  INDEX idx_login_time (login_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

-- ========================================
-- 初始化数据
-- ========================================

-- 插入超级管理员角色
INSERT INTO sys_role (role_name, role_code, description, status) VALUES
('超级管理员', 'admin', '拥有所有权限', 1)
ON DUPLICATE KEY UPDATE role_name=role_name;

-- 插入超级管理员用户（密码：admin123）
-- 插入超级管理员用户（admin）
-- 密码规则：创建日期(YYYYMMDD) + 用户名首字母
-- admin创建于2025年01月07日，密码为：20250107a
-- BCrypt加密后的密码（$2a$10$...）
INSERT INTO sys_user (username, password, real_name, status, user_type, force_change_password) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 1, 1, 1)
ON DUPLICATE KEY UPDATE username=username;

-- 注意：force_change_password=1 表示首次登录必须修改密码
-- 初始密码生成规则见文档：docs/INITIAL_USER_GUIDE.md

-- 关联用户和角色
INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id
FROM sys_user u, sys_role r
WHERE u.username = 'admin' AND r.role_code = 'admin'
ON DUPLICATE KEY UPDATE user_id=user_id;

-- 插入基础权限（示例数据）
INSERT INTO sys_permission (permission_name, permission_code, permission_type, parent_id, path, component, sort_order) VALUES
('系统管理', 'system', 1, 0, '/system', NULL, 100),
('用户管理', 'system:user', 1, (SELECT id FROM sys_permission WHERE permission_code='system'), '/system/user', 'system/user/index', 1),
('用户查询', 'system:user:list', 3, (SELECT id FROM sys_permission WHERE permission_code='system:user'), NULL, NULL, 1),
('用户新增', 'system:user:create', 2, (SELECT id FROM sys_permission WHERE permission_code='system:user'), NULL, NULL, 2),
('用户编辑', 'system:user:update', 2, (SELECT id FROM sys_permission WHERE permission_code='system:user'), NULL, NULL, 3),
('用户删除', 'system:user:delete', 2, (SELECT id FROM sys_permission WHERE permission_code='system:user'), NULL, NULL, 4),
('角色管理', 'system:role', 1, (SELECT id FROM sys_permission WHERE permission_code='system'), '/system/role', 'system/role/index', 2),
('权限管理', 'system:permission', 1, (SELECT id FROM sys_permission WHERE permission_code='system'), '/system/permission', 'system/permission/index', 3)
ON DUPLICATE KEY UPDATE permission_name=permission_name;

-- 为超级管理员角色分配所有权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r, sys_permission p
WHERE r.role_code = 'admin'
ON DUPLICATE KEY UPDATE role_id=role_id;

-- ========================================
-- 广告管理相关表（示例）
-- ========================================

-- 广告主表
CREATE TABLE IF NOT EXISTS advertiser (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '广告主ID',
  name VARCHAR(100) NOT NULL COMMENT '广告主名称',
  company_name VARCHAR(200) COMMENT '公司名称',
  contact_name VARCHAR(50) COMMENT '联系人',
  contact_phone VARCHAR(20) COMMENT '联系电话',
  contact_email VARCHAR(100) COMMENT '联系邮箱',
  status TINYINT DEFAULT 0 COMMENT '状态：0-待审核 1-正常 2-已禁用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_status (status),
  INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='广告主表';

-- 推广活动表
CREATE TABLE IF NOT EXISTS campaign (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '活动ID',
  advertiser_id BIGINT NOT NULL COMMENT '广告主ID',
  campaign_name VARCHAR(100) NOT NULL COMMENT '活动名称',
  budget DECIMAL(10,2) COMMENT '预算（元）',
  start_date DATE COMMENT '开始日期',
  end_date DATE COMMENT '结束日期',
  status TINYINT DEFAULT 0 COMMENT '状态：0-待审核 1-投放中 2-已暂停 3-已完成',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_advertiser_id (advertiser_id),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推广活动表';

-- ========================================
-- 显示初始化结果
-- ========================================
SELECT 'Database initialization completed!' AS message;
SELECT COUNT(*) AS user_count FROM sys_user;
SELECT COUNT(*) AS role_count FROM sys_role;
SELECT COUNT(*) AS permission_count FROM sys_permission;
