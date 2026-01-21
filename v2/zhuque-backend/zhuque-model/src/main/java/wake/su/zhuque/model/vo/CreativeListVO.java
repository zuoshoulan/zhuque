package wake.su.zhuque.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 创意列表VO
 */
@Data
@Schema(description = "创意列表项")
public class CreativeListVO {
    @Schema(description = "创意ID")
    private Long id;

    @Schema(description = "创意业务ID")
    private String creativeId;

    @Schema(description = "广告主ID")
    private Long advertiserId;

    @Schema(description = "创意名称")
    private String name;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "素材数量")
    private Integer materialCount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
