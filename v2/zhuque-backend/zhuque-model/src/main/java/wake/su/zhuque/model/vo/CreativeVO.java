package wake.su.zhuque.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 创意VO
 */
@Data
@Schema(description = "创意详情")
public class CreativeVO {
    @Schema(description = "创意ID")
    private Long id;

    @Schema(description = "创意业务ID")
    private String creativeId;

    @Schema(description = "广告主ID")
    private Long advertiserId;

    @Schema(description = "创意名称")
    private String name;

    @Schema(description = "创意描述")
    private String description;

    @Schema(description = "格式")
    private Integer format;

    @Schema(description = "格式名称")
    private String formatName;

    @Schema(description = "落地页URL")
    private String landingPageUrl;

    @Schema(description = "展示URL")
    private String displayUrl;

    @Schema(description = "广告主域名")
    private String advertiserDomain;

    @Schema(description = "IAB内容类别")
    private List<String> cat;

    @Schema(description = "创意属性")
    private List<Integer> attr;

    @Schema(description = "创意语言")
    private String language;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "生效时间")
    private LocalDateTime startTime;

    @Schema(description = "失效时间")
    private LocalDateTime endTime;

    @Schema(description = "素材数量")
    private Integer materialCount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "创建人")
    private String createBy;

    @Schema(description = "更新人")
    private String updateBy;
}
