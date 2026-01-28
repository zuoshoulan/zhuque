package wake.su.zhuque.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 查询投放活动Request
 */
@Data
@Schema(description = "查询投放活动请求")
public class CampaignQueryRequest {

  @Schema(description = "活动名称（模糊查询）")
  private String name;

  @Schema(description = "营销目标：1=品牌曝光/2=流量/3=转化/4=ROI")
  private Integer campaignObjective;

  @Schema(description = "状态：0=草稿/1=进行中/2=暂停/3=已完成")
  private Integer status;

  @Schema(description = "当前页")
  private Integer current = 1;

  @Schema(description = "每页大小")
  private Integer size = 10;
}
