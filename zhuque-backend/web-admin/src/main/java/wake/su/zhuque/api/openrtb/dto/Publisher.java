package wake.su.zhuque.api.openrtb.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * OpenRTB 发布商对象
 */
@Data
@Schema(description = "发布商信息对象")
public class Publisher {

  @JsonProperty("id")
  @Schema(description = "发布商唯一标识符")
  private String id;

  @JsonProperty("name")
  @Schema(description = "发布商名称")
  private String name;

  @JsonProperty("cat")
  @Schema(description = "发布商的IAB内容类别")
  private List<String> cat;

  @JsonProperty("domain")
  @Schema(description = "发布商顶级域")
  private String domain;

  @JsonProperty("ext")
  @Schema(description = "扩展字段")
  private Object ext;
}
