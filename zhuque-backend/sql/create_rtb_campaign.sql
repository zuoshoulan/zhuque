-- 投放活动表
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

CREATE TABLE `rtb_campaign` (
    -- 主键
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '投放活动ID（主键）',

    -- 归属
    `advertiser_id` BIGINT NOT NULL COMMENT '广告主ID（sys_user.id）',

    -- 基础信息
    `name` VARCHAR(255) NOT NULL COMMENT '活动名称',
    `description` TEXT COMMENT '活动描述',

    -- 营销目标（Campaign独有）
    `campaign_objective` TINYINT NOT NULL COMMENT '营销目标：1=品牌曝光/2=流量/3=转化/4=ROI',
    `campaign_goal_type` TINYINT COMMENT '目标类型：1=展示/2/点击/3=转化',
    `campaign_goal_value` BIGINT COMMENT '目标值',

    -- 总体预算（Campaign独有）
    `lifetime_budget` DECIMAL(15,2) NOT NULL COMMENT '总预算（元）',
    `lifetime_budget_used` DECIMAL(15,2) NOT NULL DEFAULT 0 COMMENT '累计已消耗（元）',

    -- 总体时间（Campaign独有）
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME NOT NULL COMMENT '结束时间',

    -- 状态：0=草稿/1=进行中/2=暂停
    -- 时间到期或预算耗尽后自动停止投放，但不改变此状态
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0=草稿/1=进行中/2=暂停',

    -- 审计字段
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by` VARCHAR(64) COMMENT '创建人',
    `update_by` VARCHAR(64) COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=正常/1=删除',

    PRIMARY KEY (`id`),
    KEY `idx_advertiser_status` (`advertiser_id`, `status`),
    KEY `idx_start_time` (`start_time`),
    KEY `idx_end_time` (`end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='投放活动表';
