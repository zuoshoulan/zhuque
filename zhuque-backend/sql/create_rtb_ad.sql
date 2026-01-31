SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

CREATE TABLE `rtb_ad` (
  -- 主键
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '广告ID（主键）',

  -- 归属
  `campaign_id` BIGINT NOT NULL COMMENT '投放活动ID',
  `ad_group_id` BIGINT NOT NULL COMMENT '广告组ID',
  `advertiser_id` BIGINT NOT NULL COMMENT '广告主ID',
  `creative_id` BIGINT NOT NULL COMMENT '创意ID（rtb_creative.id）',

  -- 基础信息
  `name` VARCHAR(255) NOT NULL COMMENT '广告名称',

  -- === 创意配置 ===
  `landing_page_url` VARCHAR(1024) COMMENT '落地页URL',
  `display_url` VARCHAR(255) COMMENT '展示URL',
  `tracking_params` JSON COMMENT '追踪参数：{"utm_source":"rtb"}',

  -- === 优先级 ===
  `weight` INT NOT NULL DEFAULT 100 COMMENT '权重，值越大分配流量越多',

  -- === 状态和优先级 ===
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0=草稿/1=进行中/2=暂停',

  -- 审计字段
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT NOT NULL DEFAULT 0,

  PRIMARY KEY (`id`),
  KEY `idx_ad_group_status` (`ad_group_id`, `status`),
  KEY `idx_campaign` (`campaign_id`),
  KEY `idx_creative_id` (`creative_id`),
  KEY `idx_advertiser_status` (`advertiser_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='广告表';
