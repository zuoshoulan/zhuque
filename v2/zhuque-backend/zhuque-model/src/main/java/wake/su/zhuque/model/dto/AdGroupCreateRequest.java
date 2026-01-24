package wake.su.zhuque.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 创建广告组Request
 *
 * @author zhuque
 * @version 1.0
 */
@Data
@Schema(description = "创建广告组请求")
public class AdGroupCreateRequest {

    @NotNull(message = "投放活动ID不能为空")
    @Schema(description = "投放活动ID", required = true)
    private Long campaignId;

    @NotBlank(message = "广告组名称不能为空")
    @Schema(description = "广告组名称", required = true)
    private String name;

    @Schema(description = "广告组描述")
    private String description;

    // === 出价设置 ===

    @NotNull(message = "出价策略不能为空")
    @Schema(description = "出价策略：1=固定CPM/2=智能出价/3=目标CPA/4=最高赢价", required = true)
    private Integer bidStrategy;

    @NotNull(message = "基础出价不能为空")
    @Schema(description = "基础出价（元/千次）", required = true)
    private BigDecimal baseBidPrice;

    @Schema(description = "最高出价上限（元）")
    private BigDecimal maxBid;

    @Schema(description = "竞价底价（元），低于此价格不参与竞价")
    private BigDecimal bidFloor;

    @Schema(description = "目标CPA（元），配合bid_strategy=3使用")
    private BigDecimal targetCpa;

    @Schema(description = "目标ROAS（倍数），例如3.5=3.5倍")
    private BigDecimal targetRoas;

    @Schema(description = "出价调整：{\"device\":{\"mobile\":1.2},\"geo\":{\"CN-11\":1.3}}")
    private String bidAdjustments;

    // === 预算控制 ===

    @Schema(description = "日预算（元），NULL=继承Campaign")
    private BigDecimal dailyBudget;

    // === 投放速度 ===

    @Schema(description = "投放速度：1=加速/2=均匀")
    private Integer deliveryMode;

    @Schema(description = "进度容差（百分比），默认10%")
    private Integer deliveryPace;

    // === 定向设置 ===

    @Schema(description = "地域定向：[\"CN-11\",\"CN-31\"]")
    private String targetingGeo;

    @Schema(description = "地域排除：[\"CN-15\"]")
    private String targetingGeoExclude;

    @Schema(description = "设备定向：[\"mobile\",\"tablet\",\"desktop\"]")
    private String targetingDevice;

    @Schema(description = "操作系统：[\"iOS\",\"Android\"]")
    private String targetingOs;

    @Schema(description = "OS版本：{\"iOS\":\">=12.0\",\"Android\":\">=8.0\"}")
    private String targetingOsVersion;

    @Schema(description = "运营商：[\"46000\",\"46002\"]")
    private String targetingCarrier;

    @Schema(description = "网络类型：[\"WiFi\",\"4G\",\"5G\"]")
    private String targetingConnectionType;

    @Schema(description = "浏览器：[\"Chrome\",\"Safari\"]")
    private String targetingBrowser;

    @Schema(description = "关键词：[\"电商\",\"购物\"]")
    private String targetingKeywords;

    @Schema(description = "排除关键词：[\"竞品\"]")
    private String targetingKeywordsExclude;

    @Schema(description = "IAB内容类别：[\"IAB24\",\"IAB24-1\"]")
    private String targetingIabCategories;

    @Schema(description = "排除IAB类别：[\"IAB25\"]")
    private String targetingIabCategoriesExclude;

    @Schema(description = "人群包ID：[\"seg_001\",\"seg_002\"]")
    private String targetingUserSegments;

    @Schema(description = "排除人群包：[\"seg_999\"]")
    private String targetingUserSegmentsExclude;

    @Schema(description = "受众类型：1=全部/2=新客/3=老客")
    private Integer targetingAudienceType;

    // === 时段定向 ===

    @Schema(description = "投放时段：1=全天/2=工作日/3=自定义")
    private Integer scheduleType;

    @Schema(description = "时段配置：{\"time_ranges\":[\"09:00-12:00\"],\"weekdays\":[1,2,3,4,5]}")
    private String scheduleConfig;

    // === 频次控制 ===

    @Schema(description = "展示频次上限（次），NULL=不限制")
    private Integer frequencyCap;

    @Schema(description = "频次周期：1=小时/2=天/3=周/4=月")
    private Integer frequencyCapPeriod;

    // === 品牌安全 ===

    @Schema(description = "品牌安全级别：1=宽松/2=中等/3=严格")
    private Integer brandSafetyLevel;

    @Schema(description = "排除类别：[\"IAB25-3\"]")
    private String brandSafetyCategoriesExclude;

    // === 优先级 ===

    @Schema(description = "优先级，值越大优先级越高")
    private Integer priority;
}
