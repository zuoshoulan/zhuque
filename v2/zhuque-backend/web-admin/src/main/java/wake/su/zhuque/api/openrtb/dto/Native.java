package wake.su.zhuque.api.openrtb.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * OpenRTB 原生广告对象
 */
@Data
@Schema(description = "原生广告对象")
public class Native {

  @JsonProperty("request")
  @Schema(description = "原生广告请求的JSON有效负载", requiredMode = Schema.RequiredMode.REQUIRED)
  private String request;

  @JsonProperty("ver")
  @Schema(description = "原生广告规范版本")
  private String ver;

  @JsonProperty("api")
  @Schema(description = "API框架")
  private List<Integer> api;

  @JsonProperty("battr")
  @Schema(description = "创意属性")
  private List<Integer> battr;

  @JsonProperty("ext")
  @Schema(description = "扩展字段")
  private Object ext;
}
