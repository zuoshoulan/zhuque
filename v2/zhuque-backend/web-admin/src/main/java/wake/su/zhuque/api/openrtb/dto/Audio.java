package wake.su.zhuque.api.openrtb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * OpenRTB 音频广告对象
 */
@Data
@Schema(description = "音频广告对象")
public class Audio {

    @JsonProperty("mimes")
    @Schema(description = "支持的MIME类型白名单", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> mimes;

    @JsonProperty("minbitrate")
    @Schema(description = "最低比特率(Kbps)")
    private Integer minbitrate;

    @JsonProperty("maxbitrate")
    @Schema(description = "最高比特率(Kbps)")
    private Integer maxbitrate;

    @JsonProperty("delivery")
    @Schema(description = "允许的内容传递方法")
    private List<Integer> delivery;

    @JsonProperty("companionad")
    @Schema(description = "伴随广告的Banner对象数组")
    private List<Banner> companionad;

    @JsonProperty("api")
    @Schema(description = "API框架")
    private List<Integer> api;

    @JsonProperty("companiontype")
    @Schema(description = "允许的伴随广告类型")
    private List<Integer> companiontype;

    @JsonProperty("maxsequence")
    @Schema(description = "最大广告序列数")
    private Integer maxsequence;

    @JsonProperty("maxextended")
    @Schema(description = "最长广告扩展时长(秒)")
    private Integer maxextended;

    @JsonProperty("minextended")
    @Schema(description = "最短广告扩展时长(秒)")
    private Integer minextended;

    @JsonProperty("feed")
    @Schema(description = "音频源类型")
    private Integer feed;

    @JsonProperty("nvol")
    @Schema(description = "标准化音量指示器")
    private Integer nvol;

    @JsonProperty("ext")
    @Schema(description = "扩展字段")
    private Object ext;
}
