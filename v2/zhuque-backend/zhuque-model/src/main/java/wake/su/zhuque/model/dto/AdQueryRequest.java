package wake.su.zhuque.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 查询广告Request
 *
 * @author zhuque
 * @version 1.0
 */
@Data
@Schema(description = "查询广告请求")
public class AdQueryRequest {

  @Schema(description = "投放活动ID")
  private Long campaignId;

  @Schema(description = "广告组ID")
  private Long adGroupId;

  @Schema(description = "创意ID")
  private Long creativeId;

  @Schema(description = "广告名称（模糊查询）")
  private String name;

  @Schema(description = "状态：0=草稿/1=进行中/2=暂停")
  private Integer status;

  @Schema(description = "当前页")
  private Integer current = 1;

  @Schema(description = "每页大小")
  private Integer size = 10;
}
