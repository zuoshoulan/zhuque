package wake.su.zhuque.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 更新广告Request
 *
 * @author zhuque
 * @version 1.0
 */
@Data
@Schema(description = "更新广告请求")
public class AdUpdateRequest {

  @NotBlank(message = "广告名称不能为空")
  @Schema(description = "广告名称", required = true)
  private String name;

  @Schema(description = "创意ID")
  private Long creativeId;

  @Schema(description = "落地页URL（留空则使用创意中的设置）")
  private String landingPageUrl;

  @Schema(description = "展示URL（留空则使用创意中的设置）")
  private String displayUrl;

  @Schema(description = "追踪参数（JSON）：{\"utm_source\":\"rtb\",\"campaign_id\":\"123\"}")
  private String trackingParams;

  @Schema(description = "权重，值越大分配流量越多")
  private Integer weight;

  @Schema(description = "状态：0=草稿/1=进行中/2=暂停")
  private Integer status;
}
