package wake.su.zhuque.api.openrtb.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * OpenRTB 竞价请求对象
 *
 * @author OpenRTB
 * @version 2.5
 */
@Data
@Schema(description = "竞价请求对象")
public class BidRequest {

  @JsonProperty("id")
  @Schema(description = "竞价请求的唯一ID", required = true)
  private String id;

  @JsonProperty("imp")
  @Schema(description = "展示对象数组", required = true)
  private List<Imp> imp;

  @JsonProperty("site")
  @Schema(description = "网站信息")
  private Site site;

  @JsonProperty("app")
  @Schema(description = "应用信息")
  private App app;

  @JsonProperty("device")
  @Schema(description = "设备信息")
  private Device device;

  @JsonProperty("user")
  @Schema(description = "用户信息")
  private User user;

  @JsonProperty("test")
  @Schema(description = "测试模式标志：0=实时模式，1=测试模式", defaultValue = "0")
  private Integer test = 0;

  @JsonProperty("at")
  @Schema(description = "拍卖类型：1=第一价格，2=第二价格加", defaultValue = "2")
  private Integer at = 2;

  @JsonProperty("tmax")
  @Schema(description = "交易所允许的最大竞价时间（毫秒）")
  private Integer tmax;

  @JsonProperty("wseat")
  @Schema(description = "允许竞价的买家席位白名单")
  private List<String> wseat;

  @JsonProperty("bseat")
  @Schema(description = "禁止竞价的买家席位黑名单")
  private List<String> bseat;

  @JsonProperty("allimps")
  @Schema(description = "是否包含所有可用展示：0=否或未知，1=是", defaultValue = "0")
  private Integer allimps = 0;

  @JsonProperty("cur")
  @Schema(description = "允许的货币数组（ISO-4217）")
  private List<String> cur;

  @JsonProperty("wlang")
  @Schema(description = "创意语言白名单（ISO-639-1-alpha-2）")
  private List<String> wlang;

  @JsonProperty("bcat")
  @Schema(description = "阻止的广告主类别（IAB内容类别）")
  private List<String> bcat;

  @JsonProperty("badv")
  @Schema(description = "阻止的广告主域名列表")
  private List<String> badv;

  @JsonProperty("bapp")
  @Schema(description = "阻止的应用程序列表")
  private List<String> bapp;

  @JsonProperty("source")
  @Schema(description = "库存源信息")
  private Source source;

  @JsonProperty("regs")
  @Schema(description = "法规信息")
  private Regs regs;

  @JsonProperty("ext")
  @Schema(description = "扩展字段")
  private Object ext;
}
