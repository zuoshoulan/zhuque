package wake.su.zhuque.api.openrtb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * OpenRTB 格式对象
 */
@Data
@Schema(description = "横幅允许尺寸对象")
public class Format {

    @JsonProperty("w")
    @Schema(description = "宽度(设备无关像素)", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer w;

    @JsonProperty("h")
    @Schema(description = "高度(设备无关像素)", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer h;

    @JsonProperty("wratio")
    @Schema(description = "相对宽度")
    private Integer wratio;

    @JsonProperty("hratio")
    @Schema(description = "相对高度")
    private Integer hratio;

    @JsonProperty("wmin")
    @Schema(description = "最小宽度")
    private Integer wmin;

    @JsonProperty("ext")
    @Schema(description = "扩展字段")
    private Object ext;
}
