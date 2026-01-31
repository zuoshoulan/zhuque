package wake.su.zhuque.api.openrtb.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * OpenRTB 法规对象 (OpenRTB 2.6更新)
 */
@Data
@Schema(description = "法规信息对象")
public class Regs {

  @JsonProperty("coppa")
  @Schema(description = "是否受COPPA法规约束：0=否，1=是")
  private Integer coppa;

  @JsonProperty("gdpr")
  @Schema(description = "是否受GDPR法规约束：0=否，1=是 (OpenRTB 2.6新增)")
  private Integer gdpr;

  @JsonProperty("gpp")
  @Schema(description = "全球隐私平台(GPP)同意字符串 (OpenRTB 2.6新增)")
  private String gpp;

  @JsonProperty("gpp_sid")
  @Schema(description = "GPP章节ID列表 (OpenRTB 2.6新增)")
  private List<Integer> gppSid;

  @JsonProperty("ext")
  @Schema(description = "扩展字段")
  private Object ext;
}
