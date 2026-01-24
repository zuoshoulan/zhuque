package wake.su.zhuque.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 查询广告组Request
 *
 * @author zhuque
 * @version 1.0
 */
@Data
@Schema(description = "查询广告组请求")
public class AdGroupQueryRequest {

    @Schema(description = "投放活动ID")
    private Long campaignId;

    @Schema(description = "广告组名称（模糊查询）")
    private String name;

    @Schema(description = "状态：0=草稿/1=进行中/2=暂停")
    private Integer status;

    @Schema(description = "出价策略：1=固定CPM/2=智能出价/3=目标CPA/4=最高赢价")
    private Integer bidStrategy;

    @Schema(description = "当前页")
    private Integer current = 1;

    @Schema(description = "每页大小")
    private Integer size = 10;
}
