-- ================================
-- 数据库表结构更新脚本
-- ================================
-- 用于修复现有数据库，添加缺失的字段
-- 注意：执行前请先备份数据库
-- 执行方式：mysql -u root -p zhuque < alter.sql
-- ================================

-- 修复 sys_user 表 - 添加 real_name 字段
-- 如果报错 "Duplicate column name"，说明字段已存在，可以忽略
ALTER TABLE `sys_user`
ADD COLUMN `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名'
AFTER `nickname`;

-- 验证字段是否添加成功
DESCRIBE `sys_user`;

-- 初始化管理员账号（如果不存在）
-- 密码是 Admin123 (BCrypt加密)
INSERT IGNORE INTO `sys_user` (`id`, `username`, `password`, `nickname`, `real_name`, `email`, `phone`, `status`)
VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '管理员', '系统管理员', 'admin@zhuque.com', '13800000001', 1)
ON DUPLICATE KEY UPDATE `username` = `username`;

-- 查看用户表数据
SELECT id, username, nickname, real_name, email, status FROM `sys_user`;
