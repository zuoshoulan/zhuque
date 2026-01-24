package wake.su.zhuque.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 广告组详情VO
 *
 * @author zhuque
 * @version 1.0
 */
@Data
@Schema(description = "广告组详情")
public class AdGroupVO {

    @Schema(description = "广告组ID")
    private Long id;

    @Schema(description = "投放活动ID")
    private Long campaignId;

    @Schema(description = "投放活动名称")
    private String campaignName;

    @Schema(description = "广告主ID")
    private Long advertiserId;

    @Schema(description = "广告组名称")
    private String name;

    @Schema(description = "广告组描述")
    private String description;

    // === 出价设置 ===

    @Schema(description = "出价策略：1=固定CPM/2=智能出价/3=目标CPA/4=最高赢价")
    private Integer bidStrategy;

    @Schema(description = "出价策略名称")
    private String bidStrategyName;

    @Schema(description = "基础出价（元/千次）")
    private BigDecimal baseBidPrice;

    @Schema(description = "最高出价上限（元）")
    private BigDecimal maxBid;

    @Schema(description = "竞价底价（元）")
    private BigDecimal bidFloor;

    @Schema(description = "目标CPA（元）")
    private BigDecimal targetCpa;

    @Schema(description = "目标ROAS（倍数）")
    private BigDecimal targetRoas;

    @Schema(description = "出价调整（JSON）")
    private String bidAdjustments;

    // === 预算控制 ===

    @Schema(description = "日预算（元）")
    private BigDecimal dailyBudget;

    @Schema(description = "今日已消耗（元）")
    private BigDecimal dailyBudgetUsed;

    @Schema(description = "日消耗百分比")
    private Integer dailyUsedPercent;

    @Schema(description = "剩余日预算（元）")
    private BigDecimal remainingDailyBudget;

    // === 投放速度 ===

    @Schema(description = "投放速度：1=加速/2=均匀")
    private Integer deliveryMode;

    @Schema(description = "投放速度名称")
    private String deliveryModeName;

    @Schema(description = "进度容差（百分比）")
    private Integer deliveryPace;

    // === 定向设置概要 ===

    @Schema(description = "地域定向（JSON）")
    private String targetingGeo;

    @Schema(description = "地域定向数量")
    private Integer targetingGeoCount;

    @Schema(description = "设备定向（JSON）")
    private String targetingDevice;

    @Schema(description = "操作系统（JSON）")
    private String targetingOs;

    @Schema(description = "人群包（JSON）")
    private String targetingUserSegments;

    @Schema(description = "人群包数量")
    private Integer targetingUserSegmentsCount;

    // === 时段定向 ===

    @Schema(description = "投放时段：1=全天/2=工作日/3=自定义")
    private Integer scheduleType;

    @Schema(description = "投放时段名称")
    private String scheduleTypeName;

    @Schema(description = "时段配置（JSON）")
    private String scheduleConfig;

    // === 频次控制 ===

    @Schema(description = "展示频次上限（次）")
    private Integer frequencyCap;

    @Schema(description = "频次周期：1=小时/2=天/3=周/4=月")
    private Integer frequencyCapPeriod;

    @Schema(description = "频次周期名称")
    private String frequencyCapPeriodName;

    @Schema(description = "频次控制描述")
    private String frequencyCapDesc;

    // === 品牌安全 ===

    @Schema(description = "品牌安全级别：1=宽松/2=中等/3=严格")
    private Integer brandSafetyLevel;

    @Schema(description = "品牌安全级别名称")
    private String brandSafetyLevelName;

    // === 状态和优先级 ===

    @Schema(description = "状态：0=草稿/1=进行中/2=暂停")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "显示状态类型：info/success/warning")
    private String displayStatusType;

    @Schema(description = "优先级")
    private Integer priority;

    // === 统计信息 ===

    @Schema(description = "广告数量")
    private Integer adCount;

    @Schema(description = "今日展现量")
    private Long todayImpressions;

    @Schema(description = "今日点击量")
    private Long todayClicks;

    @Schema(description = "今日转化数")
    private Long todayConversions;

    @Schema(description = "今日CTR（%）")
    private BigDecimal todayCtr;

    @Schema(description = "今日CVR（%）")
    private BigDecimal todayCvr;

    @Schema(description = "今日消耗（元）")
    private BigDecimal todayCost;

    @Schema(description = "累计展现量")
    private Long totalImpressions;

    @Schema(description = "累计点击量")
    private Long totalClicks;

    @Schema(description = "累计转化数")
    private Long totalConversions;

    @Schema(description = "累计CTR（%）")
    private BigDecimal totalCtr;

    @Schema(description = "累计CVR（%）")
    private BigDecimal totalCvr;

    @Schema(description = "累计消耗（元）")
    private BigDecimal totalCost;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
