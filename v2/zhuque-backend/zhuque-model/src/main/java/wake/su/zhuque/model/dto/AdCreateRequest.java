package wake.su.zhuque.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 创建广告Request
 *
 * @author zhuque
 * @version 1.0
 */
@Data
@Schema(description = "创建广告请求")
public class AdCreateRequest {

    @NotNull(message = "广告组ID不能为空")
    @Schema(description = "广告组ID", required = true)
    private Long adGroupId;

    @NotNull(message = "创意ID不能为空")
    @Schema(description = "创意ID", required = true)
    private Long creativeId;

    @NotBlank(message = "广告名称不能为空")
    @Schema(description = "广告名称", required = true)
    private String name;

    @Schema(description = "落地页URL（留空则使用创意中的设置）")
    private String landingPageUrl;

    @Schema(description = "展示URL（留空则使用创意中的设置）")
    private String displayUrl;

    @Schema(description = "追踪参数（JSON）：{\"utm_source\":\"rtb\",\"campaign_id\":\"123\"}")
    private String trackingParams;

    @Schema(description = "权重，值越大分配流量越多，默认100")
    private Integer weight;

    @Schema(description = "状态：0=草稿/1=进行中/2=暂停，默认0")
    private Integer status;
}
