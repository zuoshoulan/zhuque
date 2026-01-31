-- Set character set to handle UTF-8 comments correctly
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

-- 修改 rtb_file 表：file_id → file_uuid
USE zhuque_v2;

-- 1. 重命名字段
ALTER TABLE rtb_file
CHANGE COLUMN `file_id` `file_uuid` VARCHAR(128) NOT NULL COMMENT '文件唯一UUID';

-- 2. 修改索引名称
ALTER TABLE rtb_file
DROP INDEX `uk_file_id`,
ADD UNIQUE KEY `uk_file_uuid` (`file_uuid`) COMMENT '文件UUID唯一索引';

-- 3. 素材表的 file_id 已经是引用 rtb_file.id，不需要修改
-- rtb_material.file_id 引用的是 rtb_file.id（主键），保持不变
