-- 添加 real_name 字段到 sys_user 表
ALTER TABLE `sys_user`
ADD COLUMN `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名'
AFTER `nickname`;

-- 验证字段是否添加成功
DESCRIBE `sys_user`;
