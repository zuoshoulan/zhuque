package wake.su.zhuque.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 素材列表VO
 */
@Data
@Schema(description = "素材列表项")
public class MaterialListVO {
    @Schema(description = "素材ID")
    private Long id;

    @Schema(description = "素材业务ID")
    private String materialId;

    @Schema(description = "所属创意ID")
    private Long creativeId;

    @Schema(description = "创意名称")
    private String creativeName;

    @Schema(description = "素材名称")
    private String name;

    @Schema(description = "格式")
    private Integer format;

    @Schema(description = "格式名称")
    private String formatName;

    @Schema(description = "宽度")
    private Integer width;

    @Schema(description = "高度")
    private Integer height;

    @Schema(description = "文件URL")
    private String fileUrl;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
