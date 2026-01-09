-- ================================
-- 添加用户管理相关权限
-- ================================

-- 插入用户管理权限（如果不存在）
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_code`, `permission_name`, `permission_type`, `path`, `icon`, `sort_order`, `status`)
VALUES
(100, 0, 'user', '用户管理', 1, '/users', 'User', 100, 1),
(101, 100, 'user:list', '用户列表', 2, '/users/page', NULL, 1, 1),
(102, 100, 'user:create', '创建用户', 2, '/users', NULL, 2, 1),
(103, 100, 'user:update', '更新用户', 2, '/users/{id}', NULL, 3, 1),
(104, 100, 'user:delete', '删除用户', 2, '/users/{id}', NULL, 4, 1),
(105, 100, 'user:reset-password', '重置密码', 2, '/users/{id}/reset-password', NULL, 5, 1)
ON DUPLICATE KEY UPDATE `permission_name` = VALUES(`permission_name`);

-- 查询插入的权限
SELECT id, parent_id, permission_code, permission_name, permission_type, path, status
FROM `sys_permission`
WHERE `permission_code` LIKE 'user%';
