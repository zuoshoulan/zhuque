package wake.su.zhuque.api.openrtb.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * OpenRTB 私有市场对象
 */
@Data
@Schema(description = "私有市场对象")
public class Pmp {

  @JsonProperty("private_auction")
  @Schema(description = "私有拍卖独占性：0=非私有，1=私有")
  private Integer privateAuction;

  @JsonProperty("deals")
  @Schema(description = "交易对象数组")
  private List<Deal> deals;

  @JsonProperty("ext")
  @Schema(description = "扩展字段")
  private Object ext;
}
