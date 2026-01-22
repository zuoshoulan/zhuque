-- Set character set to handle UTF-8 comments correctly
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

-- 创建素材扩展表
USE zhuque_v2;

-- ----------------------------
-- Banner素材扩展表 (rtb_material_banner)
-- 对应OpenRTB的Bid.Banner对象
-- ----------------------------
CREATE TABLE IF NOT EXISTS `rtb_material_banner` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `material_id` BIGINT NOT NULL COMMENT '关联素材ID,rtb_material.id',

    -- === 对应OpenRTB Banner字段 ===
    `pos` INT COMMENT '位置:1=首屏/2=次屏',
    `btype` JSON COMMENT '横幅类型,JSON数组格式:[2,7]',
    `wmode` INT COMMENT '窗口模式:1=正常/2=全屏',

    -- === 扩展字段 ===
    `ext` JSON COMMENT '扩展字段',

    -- === 主键和索引 ===
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_material` (`material_id`),
    INDEX `idx_pos` (`pos`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Banner素材扩展表';

-- ----------------------------
-- Video素材扩展表 (rtb_material_video)
-- 对应OpenRTB的Bid.Video对象
-- ----------------------------
CREATE TABLE IF NOT EXISTS `rtb_material_video` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `material_id` BIGINT NOT NULL COMMENT '关联素材ID,rtb_material.id',

    -- === 对应OpenRTB Video字段 ===
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

    -- === API框架 (对应Video.api) ===
    `api` JSON COMMENT '支持的API框架,JSON数组格式:[1,2,3,4,5,6]',

    -- === 扩展字段 ===
    `ext` JSON COMMENT '扩展字段',

    -- === 主键和索引 ===
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_material` (`material_id`),
    INDEX `idx_linearity` (`linearity`),
    INDEX `idx_podid` (`podid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Video素材扩展表';

-- ----------------------------
-- Audio素材扩展表 (rtb_material_audio)
-- 对应OpenRTB的Bid.Audio对象
-- ----------------------------
CREATE TABLE IF NOT EXISTS `rtb_material_audio` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `material_id` BIGINT NOT NULL COMMENT '关联素材ID,rtb_material.id',

    -- === 对应OpenRTB Audio字段 ===
    `sequence` INT COMMENT '音频序列号,从1开始',
    `min_duration` INT COMMENT '最小音频时长(秒)',
    `max_duration` INT COMMENT '最大音频时长(秒)',
    `startdelay` INT COMMENT '开始延迟',

    -- === API框架 ===
    `api` JSON COMMENT '支持的API框架,JSON数组格式:[1,2,3,4]',

    -- === 扩展字段 ===
    `ext` JSON COMMENT '扩展字段',

    -- === 主键和索引 ===
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_material` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Audio素材扩展表';

-- ----------------------------
-- Native素材扩展表 (rtb_material_native)
-- 对应OpenRTB的Bid.Native对象
-- ----------------------------
CREATE TABLE IF NOT EXISTS `rtb_material_native` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `material_id` BIGINT NOT NULL COMMENT '关联素材ID,rtb_material.id',

    -- === 对应OpenRTB Native字段 ===
    `request_json` TEXT COMMENT '原生广告请求JSON字符串',
    `ver` VARCHAR(16) COMMENT '原生API版本',

    -- === 扩展字段 ===
    `ext` JSON COMMENT '扩展字段',

    -- === 主键和索引 ===
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_material` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Native素材扩展表';
