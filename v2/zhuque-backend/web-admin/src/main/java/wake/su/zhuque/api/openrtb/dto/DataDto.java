package wake.su.zhuque.api.openrtb.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * OpenRTB 数据对象
 */
@Data
@Schema(description = "数据对象")
public class DataDto {

  @JsonProperty("id")
  @Schema(description = "数据提供者唯一标识符")
  private String id;

  @JsonProperty("name")
  @Schema(description = "数据提供者名称")
  private String name;

  @JsonProperty("segment")
  @Schema(description = "细分对象数组")
  private List<Segment> segment;

  @JsonProperty("ext")
  @Schema(description = "扩展字段")
  private Object ext;
}
