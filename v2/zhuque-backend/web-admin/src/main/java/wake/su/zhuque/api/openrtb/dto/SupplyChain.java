package wake.su.zhuque.api.openrtb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * OpenRTB 供应链对象 (OpenRTB 2.6新增)
 * 用于追踪广告库存的完整供应链路径，符合ads.cert标准
 */
@Data
@Schema(description = "供应链对象")
public class SupplyChain {

    @JsonProperty("nodes")
    @Schema(description = "供应链节点数组", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<SupplyChainNode> nodes;

    @JsonProperty("complete")
    @Schema(description = "完整性标志：0=否，1=是", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer complete;

    @JsonProperty("ver")
    @Schema(description = "供应链版本", requiredMode = Schema.RequiredMode.REQUIRED)
    private String ver;

    @JsonProperty("ext")
    @Schema(description = "扩展字段")
    private Object ext;
}
