SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

CREATE TABLE `rtb_ad_group` (
  -- 主键
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '广告组ID（主键）',

  -- 归属（关联 rtb_campaign.id）
  `campaign_id` BIGINT NOT NULL COMMENT '投放活动ID',
  `advertiser_id` BIGINT NOT NULL COMMENT '广告主ID',

  -- 基础信息
  `name` VARCHAR(255) NOT NULL COMMENT '广告组名称',
  `description` TEXT COMMENT '广告组描述',

  -- === 出价设置 ===
  `bid_strategy` TINYINT NOT NULL DEFAULT 1 COMMENT '出价策略：1=固定CPM/2=智能出价/3=目标CPA/4=最高赢价',
  `base_bid_price` DECIMAL(10,6) NOT NULL COMMENT '基础出价（元/千次）',
  `max_bid` DECIMAL(10,6) COMMENT '最高出价上限（元）',
  `bid_floor` DECIMAL(10,6) COMMENT '竞价底价（元），低于此价格不参与竞价',
  `target_cpa` DECIMAL(10,6) COMMENT '目标CPA（元），配合bid_strategy=3使用',
  `target_roas` DECIMAL(5,2) COMMENT '目标ROAS（倍数），例如3.5=3.5倍',

  -- === 出价调整 ===
  `bid_adjustments` JSON COMMENT '出价调整：{"device":{"mobile":1.2},"geo":{"CN-11":1.3}}',

  -- === 预算控制 ===
  `daily_budget` DECIMAL(12,2) COMMENT '日预算（元），NULL=继承Campaign',
  `daily_budget_used` DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '今日已消耗（元）',

  -- === 投放速度 ===
  `delivery_mode` TINYINT NOT NULL DEFAULT 2 COMMENT '投放速度：1=加速/2=均匀',
  `delivery_pace` INT DEFAULT 10 COMMENT '进度容差（百分比），默认10%',

  -- === 定向设置 ===
  `targeting_geo` JSON COMMENT '地域定向：["CN-11","CN-31"]',
  `targeting_geo_exclude` JSON COMMENT '地域排除：["CN-15"]',
  `targeting_device` JSON COMMENT '设备定向：["mobile","tablet","desktop"]',
  `targeting_os` JSON COMMENT '操作系统：["iOS","Android"]',
  `targeting_os_version` JSON COMMENT 'OS版本：{"iOS":">=12.0","Android":">=8.0"}',
  `targeting_carrier` JSON COMMENT '运营商：["46000","46002"]',
  `targeting_connection_type` JSON COMMENT '网络类型：["WiFi","4G","5G"]',
  `targeting_browser` JSON COMMENT '浏览器：["Chrome","Safari"]',
  `targeting_keywords` JSON COMMENT '关键词：["电商","购物"]',
  `targeting_keywords_exclude` JSON COMMENT '排除关键词：["竞品"]',
  `targeting_iab_categories` JSON COMMENT 'IAB内容类别：["IAB24","IAB24-1"]',
  `targeting_iab_categories_exclude` JSON COMMENT '排除IAB类别：["IAB25"]',
  `targeting_user_segments` JSON COMMENT '人群包ID：["seg_001","seg_002"]',
  `targeting_user_segments_exclude` JSON COMMENT '排除人群包：["seg_999"]',
  `targeting_audience_type` TINYINT COMMENT '受众类型：1=全部/2=新客/3=老客',

  -- === 时段定向 ===
  `schedule_type` TINYINT DEFAULT 1 COMMENT '投放时段：1=全天/2=工作日/3=自定义',
  `schedule_config` JSON COMMENT '时段配置：{"time_ranges":["09:00-12:00"],"weekdays":[1,2,3,4,5]}',

  -- === 频次控制 ===
  `frequency_cap` INT COMMENT '展示频次上限（次），NULL=不限制',
  `frequency_cap_period` TINYINT COMMENT '频次周期：1=小时/2=天/3=周/4=月',

  -- === 品牌安全 ===
  `brand_safety_level` TINYINT COMMENT '品牌安全级别：1=宽松/2=中等/3=严格',
  `brand_safety_categories_exclude` JSON COMMENT '排除类别：["IAB25-3"]',

  -- === 状态和优先级 ===
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0=草稿/1=进行中/2=暂停',
  `priority` INT DEFAULT 0 COMMENT '优先级，值越大优先级越高（多AdGroup出价相同时使用）',

  -- 审计字段
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT NOT NULL DEFAULT 0,

  PRIMARY KEY (`id`),
  KEY `idx_campaign_status` (`campaign_id`, `status`),
  KEY `idx_advertiser_status` (`advertiser_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='广告组表';
