package wake.su.zhuque.bid.dto.openrtb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * OpenRTB 2.6 Bid Request
 *
 * <p>竞价请求的顶层对象，包含所有展示机会相关的信息。 必需字段：id, imp
 */
@Schema(description = """
    OpenRTB 2.6 竞价请求（BidRequest）

    这是实时竞价协议的核心对象，由SSP发送给DSP，包含展示机会的所有信息。

    **必需字段**：
    - id: 请求的唯一标识符
    - imp: 至少包含一个展示机会对象

    **处理要求**：
    - 建议在100ms内完成处理并返回响应
    - 如果不竞价，应返回204 No Content或设置nbr字段
    """)
public class BidRequest {

  @Schema(description = "竞价请求的唯一标识符", example = "80ce30c53c16e6ede735fe1227162d61", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  private String id;

  @Schema(description = """
      展示机会（Impression）列表

      数组中至少要有一个展示对象，每个对象代表一个可竞价的广告位。
      可以包含多个不同类型的广告位（Banner、Video、Audio、Native）。
      """, requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("imp")
  private List<Imp> imp;

  @Schema(description = """
      用户信息对象

      包含当前用户的相关信息，用于用户定向和用户识别。
      - id: 交换侧的用户ID
      - buyeruid: DSP侧的用户ID（用于重定向）
      - yob: 出生年份
      - gender: 性别（M=男, F=女, O=其他）
      """)
  @JsonProperty("user")
  private User user;

  @Schema(description = """
      设备信息对象

      包含用户设备的相关信息，用于设备定向。
      - ua: 用户代理字符串
      - ip: IP地址
      - devicetype: 设备类型（1=手机, 2=PC, 3=平板, 4=CTV等）
      - os: 操作系统
      - geo: 地理位置信息
      """)
  @JsonProperty("device")
  private Device device;

  @Schema(description = """
      站点信息对象（Web流量）

      当广告位在网页中时使用。包含网站的相关信息。
      - id: 站点ID
      - domain: 站点域名
      - page: 当前页面URL
      - cat: IAB内容类别
      - publisher: 发布商信息

      与app字段互斥，Web流量使用site，App流量使用app。
      """)
  @JsonProperty("site")
  private Site site;

  @Schema(description = """
      应用信息对象（App流量）

      当广告位在移动应用中时使用。包含应用的相关信息。
      - id: 应用ID
      - bundle: 应用包名（如com.example.app）
      - ver: 应用版本
      - publisher: 发布商信息

      与site字段互斥，Web流量使用site，App流量使用app。
      """)
  @JsonProperty("app")
  private App app;

  @Schema(description = """
      测试模式标志

      - 0: 生产模式，真实竞价
      - 1: 测试模式，用于调试和集成测试

      测试模式下的竞价不会产生实际费用。
      """, example = "0", allowableValues = { "0", "1" })
  @JsonProperty("test")
  private Integer test;

  @Schema(description = """
      竞价类型（Auction Type）

      - 1: 首价拍卖（First Price），赢家支付自己的出价
      - 2: 次价拍卖（Second Price），赢家支付第二高出价

      默认值为2（次价拍卖）。
      """, example = "1", allowableValues = { "1", "2" })
  @JsonProperty("at")
  private Integer auctionType;

  @Schema(description = """
      最大超时时间（毫秒）

      SSP期望DSP在指定时间内返回响应。
      通常设置为100ms左右，超过此时间响应可能被忽略。
      """, example = "100")
  @JsonProperty("tmax")
  private Integer timeout;

  @Schema(description = """
      支持的货币列表（ISO-4217）

      指定DSP可以使用哪些货币进行出价。
      如：["USD", "CNY", "EUR"]
      如果不指定，默认使用USD。
      """, example = "[\"CNY\", \"USD\"]")
  @JsonProperty("cur")
  private List<String> currencies;

  @Schema(description = """
      阻止的广告类别列表（Blocked Categories）

      指定DSP不应竞价的IAB内容类别。
      使用IAB OpenRTB类别体系，如：["IAB25", "IAB7-42"]
      """)
  @JsonProperty("bcat")
  private List<String> blockedCategories;

  @Schema(description = """
      阻止的广告主域名列表（Blocked Advertisers）

      指定DSP不应竞价的白名单/黑名单广告主域名。
      如：["example.com", "competitor.com"]
      """)
  @JsonProperty("badv")
  private List<String> blockedAdvertisers;

  @Schema(description = """
      扩展字段

      用于自定义数据的扩展对象，允许添加规范外的字段。
      """)
  @JsonProperty("ext")
  private Object ext;

  // Getters and Setters

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<Imp> getImp() {
    return imp;
  }

  public void setImp(List<Imp> imp) {
    this.imp = imp;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Device getDevice() {
    return device;
  }

  public void setDevice(Device device) {
    this.device = device;
  }

  public Site getSite() {
    return site;
  }

  public void setSite(Site site) {
    this.site = site;
  }

  public App getApp() {
    return app;
  }

  public void setApp(App app) {
    this.app = app;
  }

  public Integer getTest() {
    return test;
  }

  public void setTest(Integer test) {
    this.test = test;
  }

  public Integer getAuctionType() {
    return auctionType;
  }

  public void setAuctionType(Integer auctionType) {
    this.auctionType = auctionType;
  }

  public Integer getTimeout() {
    return timeout;
  }

  public void setTimeout(Integer timeout) {
    this.timeout = timeout;
  }

  public List<String> getCurrencies() {
    return currencies;
  }

  public void setCurrencies(List<String> currencies) {
    this.currencies = currencies;
  }

  public List<String> getBlockedCategories() {
    return blockedCategories;
  }

  public void setBlockedCategories(List<String> blockedCategories) {
    this.blockedCategories = blockedCategories;
  }

  public List<String> getBlockedAdvertisers() {
    return blockedAdvertisers;
  }

  public void setBlockedAdvertisers(List<String> blockedAdvertisers) {
    this.blockedAdvertisers = blockedAdvertisers;
  }

  public Object getExt() {
    return ext;
  }

  public void setExt(Object ext) {
    this.ext = ext;
  }
}
