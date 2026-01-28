package wake.su.zhuque.api.openrtb.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * OpenRTB 扩展ID对象 (OpenRTB 2.6新增) 用于ID Bridging，支持多个身份提供商之间的用户ID映射
 */
@Data
@Schema(description = "扩展ID对象")
public class ExtendedId {

  @JsonProperty("source")
  @Schema(description = "ID源域名", requiredMode = Schema.RequiredMode.REQUIRED)
  private String source;

  @JsonProperty("uids")
  @Schema(description = "UID数组", requiredMode = Schema.RequiredMode.REQUIRED)
  private List<Uid> uids;

  @JsonProperty("ext")
  @Schema(description = "扩展字段")
  private Object ext;

  /**
   * UID对象
   */
  @Data
  @Schema(description = "UID对象")
  public static class Uid {

    @JsonProperty("id")
    @Schema(description = "ID值", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @JsonProperty("atype")
    @Schema(description = "ID类型：1=网页Cookie，2=设备ID，3=用户ID")
    private Integer atype;

    @JsonProperty("ext")
    @Schema(description = "扩展字段")
    private Object ext;
  }
}
