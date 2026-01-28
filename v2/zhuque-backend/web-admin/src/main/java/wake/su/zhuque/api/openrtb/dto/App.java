package wake.su.zhuque.api.openrtb.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * OpenRTB 应用对象
 */
@Data
@Schema(description = "应用信息对象")
public class App {

  @JsonProperty("id")
  @Schema(description = "应用唯一标识符")
  private String id;

  @JsonProperty("name")
  @Schema(description = "应用名称")
  private String name;

  @JsonProperty("bundle")
  @Schema(description = "应用包名或标识符")
  private String bundle;

  @JsonProperty("domain")
  @Schema(description = "应用域名")
  private String domain;

  @JsonProperty("storeurl")
  @Schema(description = "应用商店URL")
  private String storeurl;

  @JsonProperty("cat")
  @Schema(description = "IAB内容类别")
  private List<String> cat;

  @JsonProperty("sectioncat")
  @Schema(description = "应用部分的IAB内容类别")
  private List<String> sectioncat;

  @JsonProperty("pagecat")
  @Schema(description = "应用上下文的IAB内容类别")
  private List<String> pagecat;

  @JsonProperty("ver")
  @Schema(description = "应用版本")
  private String ver;

  @JsonProperty("privacypolicy")
  @Schema(description = "是否有隐私政策：0=否，1=是")
  private Integer privacypolicy;

  @JsonProperty("paid")
  @Schema(description = "是否付费应用：0=否，1=是")
  private Integer paid;

  @JsonProperty("publisher")
  @Schema(description = "发布商信息")
  private Publisher publisher;

  @JsonProperty("content")
  @Schema(description = "内容信息")
  private Content content;

  @JsonProperty("keywords")
  @Schema(description = "关键词列表")
  private String keywords;

  @JsonProperty("ext")
  @Schema(description = "扩展字段")
  private Object ext;
}
