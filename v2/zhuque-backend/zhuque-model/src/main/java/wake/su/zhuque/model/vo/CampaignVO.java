package wake.su.zhuque.model.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 投放活动详情VO
 */
@Data
@Schema(description = "投放活动详情")
public class CampaignVO {

  @Schema(description = "活动ID")
  private Long id;

  @Schema(description = "广告主ID")
  private Long advertiserId;

  @Schema(description = "活动名称")
  private String name;

  @Schema(description = "活动描述")
  private String description;

  @Schema(description = "营销目标：1=品牌曝光/2=流量/3=转化/4=ROI")
  private Integer campaignObjective;

  @Schema(description = "营销目标名称")
  private String campaignObjectiveName;

  @Schema(description = "目标类型：1=展示/2=点击/3=转化")
  private Integer campaignGoalType;

  @Schema(description = "目标类型名称")
  private String campaignGoalTypeName;

  @Schema(description = "目标值")
  private Long campaignGoalValue;

  @Schema(description = "总预算（元）")
  private BigDecimal lifetimeBudget;

  @Schema(description = "已消耗（元）")
  private BigDecimal lifetimeBudgetUsed;

  @Schema(description = "消耗百分比")
  private Integer usedPercent;

  @Schema(description = "剩余预算（元）")
  private BigDecimal remainingBudget;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Schema(description = "开始时间")
  private LocalDateTime startTime;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Schema(description = "结束时间")
  private LocalDateTime endTime;

  @Schema(description = "投放天数")
  private Integer durationDays;

  @Schema(description = "状态：0=草稿/1=进行中/2=暂停")
  private Integer status;

  @Schema(description = "状态名称（数据库字段）")
  private String statusName;

  @Schema(description = "显示状态名称（包含已完成判断）")
  private String displayStatusName;

  @Schema(description = "显示状态类型：info/success/warning/空字符串")
  private String displayStatusType;

  @Schema(description = "广告组数量")
  private Integer adGroupCount;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Schema(description = "创建时间")
  private LocalDateTime createTime;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Schema(description = "更新时间")
  private LocalDateTime updateTime;

  @Schema(description = "创建人")
  private String createBy;

  @Schema(description = "更新人")
  private String updateBy;
}
