package wake.su.zhuque.api.openrtb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * OpenRTB 法规对象
 */
@Data
@Schema(description = "法规信息对象")
public class Regs {

    @JsonProperty("coppa")
    @Schema(description = "是否受COPPA法规约束：0=否，1=是")
    private Integer coppa;

    @JsonProperty("ext")
    @Schema(description = "扩展字段")
    private Object ext;
}
