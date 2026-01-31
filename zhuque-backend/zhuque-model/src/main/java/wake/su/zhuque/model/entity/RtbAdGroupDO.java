package wake.su.zhuque.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * 广告组表DO 三层架构：Campaign（投放活动）→ AdGroup（广告组）→ Ad（广告）
 *
 * @author zhuque
 * @version 1.0
 */
@Data
@TableName("rtb_ad_group")
public class RtbAdGroupDO {

  /**
   * 广告组ID（主键）
   */
  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  /**
   * 投放活动ID
   */
  private Long campaignId;

  /**
   * 广告主ID
   */
  private Long advertiserId;

  /**
   * 广告组名称
   */
  private String name;

  /**
   * 广告组描述
   */
  private String description;

  /**
   * 出价策略：1=固定CPM/2=智能出价/3=目标CPA/4=最高赢价
   */
  private Integer bidStrategy;

  /**
   * 基础出价（元/千次）
   */
  private BigDecimal baseBidPrice;

  /**
   * 最高出价上限（元）
   */
  private BigDecimal maxBid;

  /**
   * 竞价底价（元）
   */
  private BigDecimal bidFloor;

  /**
   * 目标CPA（元）
   */
  private BigDecimal targetCpa;

  /**
   * 目标ROAS（倍数）
   */
  private BigDecimal targetRoas;

  /**
   * 出价调整（JSON）
   */
  private String bidAdjustments;

  /**
   * 日预算（元），NULL=继承Campaign
   */
  private BigDecimal dailyBudget;

  /**
   * 今日已消耗（元）
   */
  private BigDecimal dailyBudgetUsed;

  /**
   * 投放速度：1=加速/2=均匀
   */
  private Integer deliveryMode;

  /**
   * 进度容差（百分比）
   */
  private Integer deliveryPace;

  /**
   * 地域定向（JSON）
   */
  private String targetingGeo;

  /**
   * 地域排除（JSON）
   */
  private String targetingGeoExclude;

  /**
   * 设备定向（JSON）
   */
  private String targetingDevice;

  /**
   * 操作系统（JSON）
   */
  private String targetingOs;

  /**
   * OS版本（JSON）
   */
  private String targetingOsVersion;

  /**
   * 运营商（JSON）
   */
  private String targetingCarrier;

  /**
   * 网络类型（JSON）
   */
  private String targetingConnectionType;

  /**
   * 浏览器（JSON）
   */
  private String targetingBrowser;

  /**
   * 关键词（JSON）
   */
  private String targetingKeywords;

  /**
   * 排除关键词（JSON）
   */
  private String targetingKeywordsExclude;

  /**
   * IAB内容类别（JSON）
   */
  private String targetingIabCategories;

  /**
   * 排除IAB类别（JSON）
   */
  private String targetingIabCategoriesExclude;

  /**
   * 人群包ID（JSON）
   */
  private String targetingUserSegments;

  /**
   * 排除人群包（JSON）
   */
  private String targetingUserSegmentsExclude;

  /**
   * 受众类型：1=全部/2=新客/3=老客
   */
  private Integer targetingAudienceType;

  /**
   * 投放时段：1=全天/2=工作日/3=自定义
   */
  private Integer scheduleType;

  /**
   * 时段配置（JSON）
   */
  private String scheduleConfig;

  /**
   * 展示频次上限（次）
   */
  private Integer frequencyCap;

  /**
   * 频次周期：1=小时/2=天/3=周/4=月
   */
  private Integer frequencyCapPeriod;

  /**
   * 品牌安全级别：1=宽松/2=中等/3=严格
   */
  private Integer brandSafetyLevel;

  /**
   * 排除类别（JSON）
   */
  private String brandSafetyCategoriesExclude;

  /**
   * 状态：0=草稿/1=进行中/2=暂停
   */
  private Integer status;

  /**
   * 优先级
   */
  private Integer priority;

  /**
   * 创建时间
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createTime;

  /**
   * 更新时间
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime updateTime;

  /**
   * 逻辑删除：0=正常/1=删除
   */
  @TableLogic
  private Integer deleted;
}
