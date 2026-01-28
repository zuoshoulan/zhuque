package wake.su.zhuque.api.openrtb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * OpenRTB 源对象
 */
@Data
@Schema(description = "库存源信息对象")
public class Source {

  @JsonProperty("fd")
  @Schema(description = "最终决策实体：0=交易所，1=上游源")
  private Integer fd;

  @JsonProperty("tid")
  @Schema(description = "事务ID")
  private String tid;

  @JsonProperty("pchain")
  @Schema(description = "支付ID链字符串")
  private String pchain;

  @JsonProperty("schain")
  @Schema(description = "供应链对象 (OpenRTB 2.6新增)")
  private SupplyChain schain;

  @JsonProperty("ext")
  @Schema(description = "扩展字段")
  private Object ext;
}
