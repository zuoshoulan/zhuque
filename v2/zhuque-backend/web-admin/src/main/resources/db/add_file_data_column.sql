-- 修改素材表，添加文件内容字段
-- 用于直接将素材文件存储在数据库中

USE zhuque_rtb;

-- 添加文件二进制数据字段
ALTER TABLE rtb_material
ADD COLUMN file_data LONGBLOB NULL COMMENT '文件二进制数据(直接存储文件内容)'
AFTER file_id;

-- 添加文件MD5字段，用于校验文件完整性
ALTER TABLE rtb_material
ADD COLUMN file_md5 VARCHAR(32) NULL COMMENT '文件MD5值，用于校验文件完整性'
AFTER file_size;

-- 添加索引，方便根据MD5去重
CREATE INDEX idx_file_md5 ON rtb_material(file_md5);
