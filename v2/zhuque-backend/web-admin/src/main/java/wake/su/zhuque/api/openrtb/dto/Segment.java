package wake.su.zhuque.api.openrtb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * OpenRTB 细分对象
 */
@Data
@Schema(description = "数据细分对象")
public class Segment {

    @JsonProperty("id")
    @Schema(description = "细分唯一标识符")
    private String id;

    @JsonProperty("name")
    @Schema(description = "细分名称")
    private String name;

    @JsonProperty("value")
    @Schema(description = "细分值")
    private String value;

    @JsonProperty("ext")
    @Schema(description = "扩展字段")
    private Object ext;
}
