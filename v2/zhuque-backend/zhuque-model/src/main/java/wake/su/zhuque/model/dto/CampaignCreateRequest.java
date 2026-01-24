package wake.su.zhuque.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 创建投放活动Request
 */
@Data
@Schema(description = "创建投放活动请求")
public class CampaignCreateRequest {

    @NotBlank(message = "活动名称不能为空")
    @Schema(description = "活动名称", required = true)
    private String name;

    @Schema(description = "活动描述")
    private String description;

    @NotNull(message = "营销目标不能为空")
    @Schema(description = "营销目标：1=品牌曝光/2=流量/3=转化/4=ROI", required = true)
    private Integer campaignObjective;

    @Schema(description = "目标类型：1=展示/2=点击/3=转化")
    private Integer campaignGoalType;

    @Schema(description = "目标值")
    private Long campaignGoalValue;

    @NotNull(message = "总预算不能为空")
    @Schema(description = "总预算（元）", required = true)
    private BigDecimal lifetimeBudget;

    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "开始时间", required = true)
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "结束时间", required = true)
    private LocalDateTime endTime;
}
