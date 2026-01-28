package wake.su.zhuque.model.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 广告组列表VO
 *
 * @author zhuque
 * @version 1.0
 */
@Data
@Schema(description = "广告组列表")
public class AdGroupListVO {

  @Schema(description = "广告组ID")
  private Long id;

  @Schema(description = "投放活动ID")
  private Long campaignId;

  @Schema(description = "投放活动名称")
  private String campaignName;

  @Schema(description = "广告组名称")
  private String name;

  @Schema(description = "出价策略：1=固定CPM/2=智能出价/3=目标CPA/4=最高赢价")
  private Integer bidStrategy;

  @Schema(description = "出价策略名称")
  private String bidStrategyName;

  @Schema(description = "基础出价（元/千次）")
  private BigDecimal baseBidPrice;

  @Schema(description = "日预算（元）")
  private BigDecimal dailyBudget;

  @Schema(description = "今日已消耗（元）")
  private BigDecimal dailyBudgetUsed;

  @Schema(description = "日消耗百分比")
  private Integer dailyUsedPercent;

  @Schema(description = "状态：0=草稿/1=进行中/2=暂停")
  private Integer status;

  @Schema(description = "状态名称")
  private String statusName;

  @Schema(description = "显示状态类型：info/success/warning")
  private String displayStatusType;

  @Schema(description = "广告数量")
  private Integer adCount;

  @Schema(description = "今日展现量")
  private Long todayImpressions;

  @Schema(description = "今日点击量")
  private Long todayClicks;

  @Schema(description = "今日CTR（%）")
  private BigDecimal todayCtr;

  @Schema(description = "今日消耗（元）")
  private BigDecimal todayCost;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Schema(description = "创建时间")
  private LocalDateTime createTime;
}
