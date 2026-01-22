-- Set character set to handle UTF-8 comments correctly
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

-- 创建独立的文件表
USE zhuque_v2;

CREATE TABLE `rtb_file` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `file_id` VARCHAR(128) NOT NULL COMMENT '文件唯一ID',
  `file_name` VARCHAR(255) NOT NULL COMMENT '原始文件名',
  `file_data` LONGBLOB NOT NULL COMMENT '文件二进制数据',
  `file_size` BIGINT NOT NULL COMMENT '文件大小(字节)',
  `file_md5` VARCHAR(32) NOT NULL COMMENT '文件MD5值',
  `file_type` VARCHAR(64) COMMENT '文件MIME类型',
  `width` INT COMMENT '宽度(像素) - 图片/视频有效',
  `height` INT COMMENT '高度(像素) - 图片/视频有效',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_file_id` (`file_id`),
  UNIQUE KEY `uk_file_md5` (`file_md5`) COMMENT 'MD5唯一索引，用于去重',
  INDEX `idx_file_name` (`file_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件表-存储上传的文件内容';

-- 素材表修改：file_id 改为引用 rtb_file 表
ALTER TABLE rtb_material
MODIFY COLUMN `file_id` VARCHAR(128) NOT NULL COMMENT '关联文件ID,rtb_file.file_id';
