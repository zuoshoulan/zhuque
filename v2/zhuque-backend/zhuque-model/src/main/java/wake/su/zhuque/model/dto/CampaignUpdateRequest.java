package wake.su.zhuque.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 更新投放活动Request
 */
@Data
@Schema(description = "更新投放活动请求")
public class CampaignUpdateRequest {

    @NotBlank(message = "活动名称不能为空")
    @Schema(description = "活动名称", required = true)
    private String name;

    @Schema(description = "活动描述")
    private String description;

    @Schema(description = "营销目标：1=品牌曝光/2=流量/3=转化/4=ROI")
    private Integer campaignObjective;

    @Schema(description = "目标类型：1=展示/2=点击/3=转化")
    private Integer campaignGoalType;

    @Schema(description = "目标值")
    private Long campaignGoalValue;

    @Schema(description = "总预算（元）")
    private BigDecimal lifetimeBudget;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "结束时间")
    private LocalDateTime endTime;
}
