package wake.su.zhuque.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 素材VO
 */
@Data
@Schema(description = "素材详情")
public class MaterialVO {
    @Schema(description = "素材ID")
    private Long id;

    @Schema(description = "素材业务ID")
    private String materialId;

    @Schema(description = "所属创意ID")
    private Long creativeId;

    @Schema(description = "创意名称")
    private String creativeName;

    @Schema(description = "广告主ID")
    private Long advertiserId;

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

    @Schema(description = "文件大小")
    private Long fileSize;

    @Schema(description = "文件类型")
    private String fileType;

    @Schema(description = "文件URL")
    private String fileUrl;

    @Schema(description = "缩略图URL")
    private String thumbnailUrl;

    @Schema(description = "支持的MIME类型")
    private List<String> mimes;

    @Schema(description = "时长")
    private Integer dur;

    @Schema(description = "Banner扩展信息")
    private Map<String, Object> bannerExt;

    @Schema(description = "Video扩展信息")
    private Map<String, Object> videoExt;

    @Schema(description = "Audio扩展信息")
    private Map<String, Object> audioExt;

    @Schema(description = "Native扩展信息")
    private Map<String, Object> nativeExt;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
