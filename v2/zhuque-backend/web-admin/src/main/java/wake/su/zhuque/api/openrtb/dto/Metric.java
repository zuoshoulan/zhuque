package wake.su.zhuque.api.openrtb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * OpenRTB 度量对象
 */
@Data
@Schema(description = "度量对象")
public class Metric {

    @JsonProperty("type")
    @Schema(description = "度量类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;

    @JsonProperty("value")
    @Schema(description = "度量值(概率范围0.0-1.0)", requiredMode = Schema.RequiredMode.REQUIRED)
    private Float value;

    @JsonProperty("vendor")
    @Schema(description = "值源")
    private String vendor;

    @JsonProperty("ext")
    @Schema(description = "扩展字段")
    private Object ext;
}
