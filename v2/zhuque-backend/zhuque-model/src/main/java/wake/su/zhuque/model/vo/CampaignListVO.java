package wake.su.zhuque.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 投放活动列表VO
 */
@Data
@Schema(description = "投放活动列表")
public class CampaignListVO {

    @Schema(description = "活动ID")
    private Long id;

    @Schema(description = "活动名称")
    private String name;

    @Schema(description = "营销目标：1=品牌曝光/2=流量/3=转化/4=ROI")
    private Integer campaignObjective;

    @Schema(description = "营销目标名称")
    private String campaignObjectiveName;

    @Schema(description = "总预算（元）")
    private BigDecimal lifetimeBudget;

    @Schema(description = "已消耗（元）")
    private BigDecimal lifetimeBudgetUsed;

    @Schema(description = "消耗百分比")
    private Integer usedPercent;

    @Schema(description = "状态：0=草稿/1=进行中/2=暂停")
    private Integer status;

    @Schema(description = "状态名称（数据库字段）")
    private String statusName;

    @Schema(description = "显示状态名称（包含已完成判断）")
    private String displayStatusName;

    @Schema(description = "显示状态类型：info/success/warning/空字符串")
    private String displayStatusType;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "时间范围文本")
    private String timeRange;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
