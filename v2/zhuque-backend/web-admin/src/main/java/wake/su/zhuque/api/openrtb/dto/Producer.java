package wake.su.zhuque.api.openrtb.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * OpenRTB 制作者对象
 */
@Data
@Schema(description = "内容制作者信息对象")
public class Producer {

  @JsonProperty("id")
  @Schema(description = "制作者唯一标识符")
  private String id;

  @JsonProperty("name")
  @Schema(description = "制作者名称")
  private String name;

  @JsonProperty("cat")
  @Schema(description = "制作者的IAB内容类别")
  private List<String> cat;

  @JsonProperty("domain")
  @Schema(description = "制作者顶级域")
  private String domain;

  @JsonProperty("ext")
  @Schema(description = "扩展字段")
  private Object ext;
}
