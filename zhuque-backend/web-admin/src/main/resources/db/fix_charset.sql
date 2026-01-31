-- =============================================
-- 修复数据库表字符集和注释乱码
-- =============================================

-- 删除旧表（如果存在）
DROP TABLE IF EXISTS `rtb_material_native`;
DROP TABLE IF EXISTS `rtb_material_audio`;
DROP TABLE IF EXISTS `rtb_material_video`;
DROP TABLE IF EXISTS `rtb_material_banner`;
DROP TABLE IF EXISTS `rtb_material`;
DROP TABLE IF EXISTS `rtb_creative`;

-- ----------------------------
-- 1. 创意表 (rtb_creative)
-- ----------------------------
CREATE TABLE `rtb_creative` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `creative_id` VARCHAR(64) NOT NULL COMMENT '创意ID,对应OpenRTB的crid',
    `advertiser_id` BIGINT NOT NULL COMMENT '广告主ID,对应OpenRTB的adid',
    `name` VARCHAR(255) NOT NULL COMMENT '创意名称',
    `description` TEXT COMMENT '创意描述',
    `landing_page_url` VARCHAR(1024) NOT NULL COMMENT '落地页URL',
    `display_url` VARCHAR(512) NOT NULL COMMENT '展示URL',
    `advertiser_domain` VARCHAR(255) COMMENT '广告主域名,对应OpenRTB的adomain',
    `cat` JSON COMMENT 'IAB内容类别,JSON数组格式:["IAB1","IAB2"]',
    `attr` JSON COMMENT '创意属性,JSON数组格式:[1,2,3]',
    `language` VARCHAR(8) COMMENT '创意语言,ISO-639-1-alpha-2',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态:0=草稿/1=启用/2=停用',
    `start_time` DATETIME COMMENT '生效时间',
    `end_time` DATETIME COMMENT '失效时间',
    `ext` JSON COMMENT '扩展字段',
    `remark` VARCHAR(512) COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(64) COMMENT '创建人',
    `update_by` VARCHAR(64) COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=正常/1=删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_creative_id` (`creative_id`),
    INDEX `idx_advertiser` (`advertiser_id`),
    INDEX `idx_status_time` (`status`, `start_time`, `end_time`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='创意表-对应OpenRTB的Creative';

-- ----------------------------
-- 2. 素材表 (rtb_material)
-- ----------------------------
CREATE TABLE `rtb_material` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `material_id` VARCHAR(64) NOT NULL COMMENT '素材唯一ID',
    `creative_id` BIGINT NOT NULL COMMENT '关联创意ID,rtb_creative.id',
    `advertiser_id` BIGINT NOT NULL COMMENT '广告主ID',
    `name` VARCHAR(255) NOT NULL COMMENT '素材名称',
    `format` TINYINT NOT NULL COMMENT '格式:1=Banner/2=Video/3=Audio/4=Native',
    `width` INT NOT NULL COMMENT '素材宽度(像素),对应Bid.w',
    `height` INT NOT NULL COMMENT '素材高度(像素),对应Bid.h',
    `ratio` INT COMMENT '宽高比(例如100表示1:1),对应Bid.ratio',
    `file_id` VARCHAR(128) NOT NULL COMMENT 'OSS文件ID',
    `file_size` BIGINT COMMENT '文件大小(字节)',
    `file_type` VARCHAR(64) COMMENT '文件MIME类型',
    `thumbnail_url` VARCHAR(512) COMMENT '缩略图URL(视频/音频用)',
    `dur` INT COMMENT '视频或音频持续时间(秒),对应Bid.dur',
    `mimes` JSON COMMENT '支持的MIME类型,JSON数组格式:["image/jpeg","image/png"]',
    `protocol` INT COMMENT '视频响应协议:1=VAST1.0/2=VAST2.0/3=VAST3.0/4=VAST4.0',
    `ext` JSON COMMENT '扩展字段',
    `remark` VARCHAR(512) COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(64) COMMENT '创建人',
    `update_by` VARCHAR(64) COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=正常/1=删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_material_id` (`material_id`),
    INDEX `idx_creative` (`creative_id`),
    INDEX `idx_advertiser` (`advertiser_id`),
    INDEX `idx_format_size` (`format`, `width`, `height`),
    INDEX `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='素材表-对应OpenRTB的Ad Markup中的文件';

-- ----------------------------
-- 3. Banner素材扩展表
-- ----------------------------
CREATE TABLE `rtb_material_banner` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `material_id` BIGINT NOT NULL COMMENT '关联素材ID,rtb_material.id',
    `pos` INT COMMENT '位置:1=首屏/2=次屏',
    `btype` JSON COMMENT '横幅类型,JSON数组格式:[1,2,3]',
    `wmode` INT COMMENT '窗口模式:1=正常/2=全屏',
    `ext` JSON COMMENT '扩展字段',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_material` (`material_id`),
    INDEX `idx_pos` (`pos`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Banner素材扩展表';

-- ----------------------------
-- 4. Video素材扩展表
-- ----------------------------
CREATE TABLE `rtb_material_video` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `material_id` BIGINT NOT NULL COMMENT '关联素材ID,rtb_material.id',
    `linearity` TINYINT NOT NULL COMMENT '1=线性/2=非线性',
    `sequence` INT COMMENT '视频序列号,从1开始',
    `min_duration` INT COMMENT '最小视频时长(秒)',
    `max_duration` INT COMMENT '最大视频时长(秒)',
    `startdelay` INT COMMENT '前贴片:0,中贴片:-1,后贴片:>0',
    `skip` INT COMMENT '跳过按钮:0=不可跳过/1=可跳过',
    `skipmin` INT COMMENT '最少播放多少秒后可跳过',
    `skipafter` INT COMMENT '多少秒后显示跳过按钮',
    `placement` TINYINT COMMENT '1=流内/2=插屏/3=悬停',
    `playbackend` INT COMMENT '播放方法:1=自动播放有声/2=自动播放静音/3=点击播放/4=鼠标悬停',
    `playableafter` INT COMMENT '可播放的最长秒数 (OpenRTB 2.6新增)',
    `podid` VARCHAR(64) COMMENT '广告组ID (OpenRTB 2.6新增)',
    `podsize` INT COMMENT '广告组大小 (OpenRTB 2.6新增)',
    `podseq` INT COMMENT '广告组序列号 (OpenRTB 2.6新增)',
    `mincpmpersec` DECIMAL(10,2) COMMENT '每秒最低CPM (OpenRTB 2.6新增)',
    `maxseq` INT COMMENT '最大序列号 (OpenRTB 2.6新增)',
    `render` INT COMMENT '渲染方式 (OpenRTB 2.6新增)',
    `api` JSON COMMENT '支持的API框架,JSON数组格式:[1,2,3,4,5,6]',
    `ext` JSON COMMENT '扩展字段',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_material` (`material_id`),
    INDEX `idx_linearity` (`linearity`),
    INDEX `idx_podid` (`podid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Video素材扩展表';

-- ----------------------------
-- 5. Audio素材扩展表
-- ----------------------------
CREATE TABLE `rtb_material_audio` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `material_id` BIGINT NOT NULL COMMENT '关联素材ID,rtb_material.id',
    `sequence` INT COMMENT '音频序列号,从1开始',
    `min_duration` INT COMMENT '最小音频时长(秒)',
    `max_duration` INT COMMENT '最大音频时长(秒)',
    `startdelay` INT COMMENT '开始延迟',
    `api` JSON COMMENT '支持的API框架,JSON数组格式:[1,2,3,4]',
    `ext` JSON COMMENT '扩展字段',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_material` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Audio素材扩展表';

-- ----------------------------
-- 6. Native素材扩展表
-- ----------------------------
CREATE TABLE `rtb_material_native` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `material_id` BIGINT NOT NULL COMMENT '关联素材ID,rtb_material.id',
    `request_json` TEXT COMMENT '原生广告请求JSON字符串',
    `ver` VARCHAR(16) COMMENT '原生API版本',
    `ext` JSON COMMENT '扩展字段',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_material` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Native素材扩展表';

-- ----------------------------
-- 测试数据
-- ----------------------------

-- 插入测试创意
INSERT INTO `rtb_creative` (
    `creative_id`, `advertiser_id`, `name`, `description`,
    `landing_page_url`, `display_url`, `advertiser_domain`,
    `cat`, `language`, `status`, `create_by`
) VALUES
('C001', 1, '双11促销Banner活动', '双11电商促销活动Banner',
 'https://example.com/promo11', 'example.com', 'example.com',
 '["IAB24","IAB24-1"]', 'zh-CN', 1, 'admin');

-- 插入测试素材
INSERT INTO `rtb_material` (
    `material_id`, `creative_id`, `advertiser_id`, `name`,
    `format`, `width`, `height`, `file_id`, `file_type`,
    `mimes`, `create_by`
) VALUES
('M001', 1, 1, '双11促销 728x90 版本A', 1, 728, 90, 'oss://banner/728x90.jpg', 'image/jpeg',
 '["image/jpeg","image/png"]', 'admin'),
('M002', 1, 1, '双11促销 300x250 版本A', 1, 300, 250, 'oss://banner/300x250.jpg', 'image/jpeg',
 '["image/jpeg","image/png"]', 'admin');

-- 插入Banner扩展数据
INSERT INTO `rtb_material_banner` (`material_id`, `pos`, `btype`, `wmode`) VALUES
(1, 1, '[2,3]', 1),
(2, 2, '[2]', 1);
