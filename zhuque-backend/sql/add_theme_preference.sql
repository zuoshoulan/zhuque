-- 添加用户主题偏好字段（使用INT存储）
ALTER TABLE `sys_user` ADD COLUMN `theme_preference` INT DEFAULT 0 COMMENT '主题偏好: 0-自动, 1-亮色, 2-暗色' AFTER `update_by`;

-- 更新现有用户默认为自动模式(0)
UPDATE `sys_user` SET `theme_preference` = 0 WHERE `theme_preference` IS NULL;
