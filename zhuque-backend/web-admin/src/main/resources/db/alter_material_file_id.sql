-- Set character set to handle UTF-8 comments correctly
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

-- 修改 rtb_material 表的 file_id 字段类型：VARCHAR -> BIGINT
USE zhuque_v2;

-- 先清空旧数据（因为旧的 file_id 存储的是 UUID，无法转换成 id）
TRUNCATE TABLE rtb_material;

-- 修改字段类型
ALTER TABLE rtb_material
MODIFY COLUMN `file_id` BIGINT NOT NULL COMMENT '关联文件ID,rtb_file.id';

-- 重新创建索引（如果有的话）
-- ALTER TABLE rtb_material ADD INDEX `idx_file_id` (`file_id`);
