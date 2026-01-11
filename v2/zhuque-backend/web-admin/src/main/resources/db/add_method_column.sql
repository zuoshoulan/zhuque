-- 为 sys_permission 表添加 method 字段
-- 执行时间: 2026-01-11
-- 说明: 修复 sys_permission 表缺少 method 字段的问题

ALTER TABLE `sys_permission`
ADD COLUMN `method` VARCHAR(10) DEFAULT NULL COMMENT 'HTTP方法：GET/POST/PUT/DELETE等'
AFTER `path`;
