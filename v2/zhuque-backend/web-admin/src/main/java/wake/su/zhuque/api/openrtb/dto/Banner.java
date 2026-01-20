package wake.su.zhuque.api.openrtb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * OpenRTB 横幅广告对象
 */
@Data
@Schema(description = "横幅广告对象")
public class Banner {

    @JsonProperty("format")
    @Schema(description = "允许的横幅尺寸数组")
    private List<Format> format;

    @JsonProperty("w")
    @Schema(description = "确切宽度(设备无关像素)")
    private Integer w;

    @JsonProperty("h")
    @Schema(description = "确切高度(设备无关像素)")
    private Integer h;

    @JsonProperty("wmax")
    @Schema(description = "最大宽度(已弃用)")
    @Deprecated
    private Integer wmax;

    @JsonProperty("hmax")
    @Schema(description = "最大高度(已弃用)")
    @Deprecated
    private Integer hmax;

    @JsonProperty("wmin")
    @Schema(description = "最小宽度(已弃用)")
    @Deprecated
    private Integer wmin;

    @JsonProperty("hmin")
    @Schema(description = "最小高度(已弃用)")
    @Deprecated
    private Integer hmin;

    @JsonProperty("btype")
    @Schema(description = "横幅广告类型")
    private List<Integer> btype;

    @JsonProperty("battr")
    @Schema(description = "创意属性")
    private List<Integer> battr;

    @JsonProperty("pos")
    @Schema(description = "广告位置")
    private Integer pos;

    @JsonProperty("mimes")
    @Schema(description = "支持的MIME类型白名单")
    private List<String> mimes;

    @JsonProperty("topframe")
    @Schema(description = "是否在顶级浏览器窗口显示：0=否，1=是")
    private Integer topframe;

    @JsonProperty("expdir")
    @Schema(description = "展开方向")
    private List<Integer> expdir;

    @JsonProperty("api")
    @Schema(description = "API框架")
    private List<Integer> api;

    @JsonProperty("id")
    @Schema(description = "交换特定的唯一标识符")
    private String id;

    @JsonProperty("vcm")
    @Schema(description = "视频创意模块数")
    private Integer vcm;

    @JsonProperty("ext")
    @Schema(description = "扩展字段")
    private Object ext;
}
