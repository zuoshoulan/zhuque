package wake.su.zhuque.api.openrtb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * OpenRTB 供应链节点对象 (OpenRTB 2.6新增)
 */
@Data
@Schema(description = "供应链节点对象")
public class SupplyChainNode {

    @JsonProperty("asi")
    @Schema(description = "广告系统标识符(域名)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String asi;

    @JsonProperty("sid")
    @Schema(description = "供应链节点ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String sid;

    @JsonProperty("hp")
    @Schema(description = "付费类型：0=交换/中间人，1=卖方，2=买方", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer hp;

    @JsonProperty("rid")
    @Schema(description = "关联到源的第三方唯一ID")
    private String rid;

    @JsonProperty("name")
    @Schema(description = "供应链节点名称")
    private String name;

    @JsonProperty("domain")
    @Schema(description = "供应链节点域名")
    private String domain;

    @JsonProperty("cat")
    @Schema(description = "IAB内容类别列表")
    private List<String> cat;

    @JsonProperty("ext")
    @Schema(description = "扩展字段")
    private Object ext;
}
