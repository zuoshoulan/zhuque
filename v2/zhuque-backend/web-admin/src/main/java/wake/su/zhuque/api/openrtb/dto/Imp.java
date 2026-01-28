package wake.su.zhuque.api.openrtb.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * OpenRTB 展示对象
 *
 * @author OpenRTB
 * @version 2.5
 */
@Data
@Schema(description = "展示对象")
public class Imp {

  @JsonProperty("id")
  @Schema(description = "展示的唯一标识符", requiredMode = Schema.RequiredMode.REQUIRED)
  private String id;

  @JsonProperty("metric")
  @Schema(description = "度量对象数组")
  private List<Metric> metric;

  @JsonProperty("banner")
  @Schema(description = "横幅广告对象")
  private Banner banner;

  @JsonProperty("video")
  @Schema(description = "视频广告对象")
  private Video video;

  @JsonProperty("audio")
  @Schema(description = "音频广告对象")
  private Audio audio;

  @JsonProperty("native")
  @Schema(description = "原生广告对象")
  private Native native_;

  @JsonProperty("pmp")
  @Schema(description = "私有市场对象")
  private Pmp pmp;

  @JsonProperty("displaymanager")
  @Schema(description = "广告展示管理器名称")
  private String displaymanager;

  @JsonProperty("displaymanagerver")
  @Schema(description = "广告展示管理器版本")
  private String displaymanagerver;

  @JsonProperty("instl")
  @Schema(description = "是否插页广告：0=否，1=是", defaultValue = "0")
  private Integer instl = 0;

  @JsonProperty("tagid")
  @Schema(description = "广告位标识符")
  private String tagid;

  @JsonProperty("bidfloor")
  @Schema(description = "最低竞价价格(CPM)", defaultValue = "0")
  private Float bidfloor = 0f;

  @JsonProperty("bidfloorcur")
  @Schema(description = "竞价货币(ISO-4217)", defaultValue = "USD")
  private String bidfloorcur = "USD";

  @JsonProperty("clickbrowser")
  @Schema(description = "点击打开的浏览器类型：0=嵌入式，1=原生")
  private Integer clickbrowser;

  @JsonProperty("secure")
  @Schema(description = "是否需要HTTPS：0=非安全，1=安全")
  private Integer secure;

  @JsonProperty("iframebuster")
  @Schema(description = "支持的iframe buster列表")
  private List<String> iframebuster;

  @JsonProperty("exp")
  @Schema(description = "拍卖到实际展示的建议秒数")
  private Integer exp;

  @JsonProperty("ext")
  @Schema(description = "扩展字段")
  private Object ext;
}
