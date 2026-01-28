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
      阻止的应用列表（Blocked Apps）

      指定DSP不应竞价的应用包名列表。
      如：["com.blocked.app1", "com.blocked.app2"]
      """)
  @JsonProperty("bapp")
  private List<String> blockedApps;

  @Schema(description = """
      阻止的类别数组（Blocked Categories with Attributes）

      指定带有属性的阻止类别数组，用于更细粒度的类别控制。
      """)
  @JsonProperty("bcatype")
  private List<String> blockedCategoriesWithAttributes;

  @Schema(description = """
      接受的创作语言列表（Allowed Languages）

      指定DSP可以返回的创意语言，使用ISO-639-1-alpha-2代码。
      如：["zh-CN", "en-US", "ja"]
      """)
  @JsonProperty("wlang")
  private List<String> allowedLanguages;

  @Schema(description = """
      库存源对象（Source）

      包含库存来源的信息，用于供应链追踪和验证。
      - id: 源ID
      - tid: 交易ID
      - pchain: 付费链
      - schain: 供应链对象（OpenRTB 2.6新增）
      """)
  @JsonProperty("source")
  private Source source;

  @Schema(description = """
      法规对象（Regulations）

      包含隐私和广告相关的法规合规信息。
      - coppa: COPPA合规（儿童在线隐私保护法）
      - gdpr: GDPR合规
      - gpp: 全球隐私平台同意字符串（OpenRTB 2.6新增）
      - gpp_sid: GPP章节ID列表
      """)
  @JsonProperty("regs")
  private Regs regs;

  @Schema(description = """
      限制对象（Restrictions）

      包含额外的竞价限制和规则。
      """)
  @JsonProperty("restrictions")
  private Restrictions restrictions;

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

  public List<String> getBlockedApps() {
    return blockedApps;
  }

  public void setBlockedApps(List<String> blockedApps) {
    this.blockedApps = blockedApps;
  }

  public List<String> getBlockedCategoriesWithAttributes() {
    return blockedCategoriesWithAttributes;
  }

  public void setBlockedCategoriesWithAttributes(List<String> blockedCategoriesWithAttributes) {
    this.blockedCategoriesWithAttributes = blockedCategoriesWithAttributes;
  }

  public List<String> getAllowedLanguages() {
    return allowedLanguages;
  }

  public void setAllowedLanguages(List<String> allowedLanguages) {
    this.allowedLanguages = allowedLanguages;
  }

  public Source getSource() {
    return source;
  }

  public void setSource(Source source) {
    this.source = source;
  }

  public Regs getRegs() {
    return regs;
  }

  public void setRegs(Regs regs) {
    this.regs = regs;
  }

  public Restrictions getRestrictions() {
    return restrictions;
  }

  public void setRestrictions(Restrictions restrictions) {
    this.restrictions = restrictions;
  }

  // ==================== 内部类定义 ====================

  @Schema(description = """
      库存源对象（Source）

      用于追踪广告库存的来源，包含供应链信息。
      """)
  public static class Source {
    @Schema(description = "源ID", example = "source-001")
    @JsonProperty("id")
    private String id;

    @Schema(description = """
        交易ID（Transaction ID）

        用于追踪和关联具体交易的标识符。
        """, example = "txn-12345")
    @JsonProperty("tid")
    private String transactionId;

    @Schema(description = """
        付费链（Payment Chain）

        用于追踪付费关系的链式标识符。
        """)
    @JsonProperty("pchain")
    private String paymentChain;

    @Schema(description = """
        供应链对象（Supply Chain）

        OpenRTB 2.6新增，用于完整追踪广告库存的供应链路径。
        符合IAB ads.cert标准。
        """)
    @JsonProperty("schain")
    private SupplyChain supplyChain;

    @Schema(description = """
        SKAdNetwork归因域名

        用于iOS广告归因的域名。
        """)
    @JsonProperty("skhadomain")
    private String skAdNetworkDomain;

    @Schema(description = "扩展字段")
    @JsonProperty("ext")
    private Object ext;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getTransactionId() {
      return transactionId;
    }

    public void setTransactionId(String transactionId) {
      this.transactionId = transactionId;
    }

    public String getPaymentChain() {
      return paymentChain;
    }

    public void setPaymentChain(String paymentChain) {
      this.paymentChain = paymentChain;
    }

    public SupplyChain getSupplyChain() {
      return supplyChain;
    }

    public void setSupplyChain(SupplyChain supplyChain) {
      this.supplyChain = supplyChain;
    }

    public String getSkAdNetworkDomain() {
      return skAdNetworkDomain;
    }

    public void setSkAdNetworkDomain(String skAdNetworkDomain) {
      this.skAdNetworkDomain = skAdNetworkDomain;
    }

    public Object getExt() {
      return ext;
    }

    public void setExt(Object ext) {
      this.ext = ext;
    }
  }

  @Schema(description = """
      供应链对象（Supply Chain）

      OpenRTB 2.6新增，用于追踪广告库存的完整供应链路径。
      """)
  public static class SupplyChain {
    @Schema(description = """
        完整性标志

        - 0: 不完整，供应链中间可能有缺失环节
        - 1: 完整，所有中间环节都已包含
        """, requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("complete")
    private Integer complete;

    @Schema(description = """
        供应链节点列表

        按顺序排列的所有供应链环节节点。
        """, requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("nodes")
    private List<SupplyChainNode> nodes;

    @Schema(description = """
        供应链版本

        指定使用的供应链规范版本。
        """, example = "1.0")
    @JsonProperty("ver")
    private String version;

    @Schema(description = "扩展字段")
    @JsonProperty("ext")
    private Object ext;

    public Integer getComplete() {
      return complete;
    }

    public void setComplete(Integer complete) {
      this.complete = complete;
    }

    public List<SupplyChainNode> getNodes() {
      return nodes;
    }

    public void setNodes(List<SupplyChainNode> nodes) {
      this.nodes = nodes;
    }

    public String getVersion() {
      return version;
    }

    public void setVersion(String version) {
      this.version = version;
    }

    public Object getExt() {
      return ext;
    }

    public void setExt(Object ext) {
      this.ext = ext;
    }
  }

  @Schema(description = """
      供应链节点对象（SupplyChain Node）

      描述供应链中的单个环节。
      """)
  public static class SupplyChainNode {
    @Schema(description = """
        广告系统标识符（Advertising System Identifier）

        该环节实体的域名，如：publisher.com, ssp.com
        """, requiredMode = Schema.RequiredMode.REQUIRED, example = "publisher.com")
    @JsonProperty("asi")
    private String advertisingSystemIdentifier;

    @Schema(description = """
        供应链节点ID（Supply Chain Node ID）

        该环节实体的唯一标识符。
        """, requiredMode = Schema.RequiredMode.REQUIRED, example = "pub-123")
    @JsonProperty("sid")
    private String nodeId;

    @Schema(description = """
        付费类型（HP - Handling Payment）

        - 0: 交换/中间人
        - 1: 卖方
        - 2: 买方
        """, requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("hp")
    private Integer handlingPayment;

    @Schema(description = """
        关联到源的第三方唯一ID（Related Resource ID）
        """)
    @JsonProperty("rid")
    private String relatedResourceId;

    @Schema(description = "供应链节点名称", example = "Publisher Inc.")
    @JsonProperty("name")
    private String name;

    @Schema(description = "供应链节点域名", example = "publisher.com")
    @JsonProperty("domain")
    private String domain;

    @Schema(description = "IAB内容类别列表")
    @JsonProperty("cat")
    private List<String> categories;

    @Schema(description = "扩展字段")
    @JsonProperty("ext")
    private Object ext;

    public String getAdvertisingSystemIdentifier() {
      return advertisingSystemIdentifier;
    }

    public void setAdvertisingSystemIdentifier(String advertisingSystemIdentifier) {
      this.advertisingSystemIdentifier = advertisingSystemIdentifier;
    }

    public String getNodeId() {
      return nodeId;
    }

    public void setNodeId(String nodeId) {
      this.nodeId = nodeId;
    }

    public Integer getHandlingPayment() {
      return handlingPayment;
    }

    public void setHandlingPayment(Integer handlingPayment) {
      this.handlingPayment = handlingPayment;
    }

    public String getRelatedResourceId() {
      return relatedResourceId;
    }

    public void setRelatedResourceId(String relatedResourceId) {
      this.relatedResourceId = relatedResourceId;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getDomain() {
      return domain;
    }

    public void setDomain(String domain) {
      this.domain = domain;
    }

    public List<String> getCategories() {
      return categories;
    }

    public void setCategories(List<String> categories) {
      this.categories = categories;
    }

    public Object getExt() {
      return ext;
    }

    public void setExt(Object ext) {
      this.ext = ext;
    }
  }

  @Schema(description = """
      法规对象（Regulations）

      包含隐私和广告相关的法规合规信息。
      """)
  public static class Regs {
    @Schema(description = """
        COPPA合规标志

        - 0: 非儿童定向内容
        - 1: 儿童定向内容（需遵守COPPA）
        """, example = "0")
    @JsonProperty("coppa")
    private Integer coppa;

    @Schema(description = """
        GDPR合规标志（扩展字段）

        - 0: GDPR不适用
        - 1: GDPR适用
        """)
    @JsonProperty("gdpr")
    private Integer gdpr;

    @Schema(description = """
        全球隐私平台同意字符串（Global Privacy Platform）

        OpenRTB 2.6新增，用于统一的全球隐私合规框架。
        """)
    @JsonProperty("gpp")
    private String gpp;

    @Schema(description = """
        GPP章节ID列表

        指定适用的GPP规范章节。
        如：[3, 6] 表示TCF v2和US Privacy
        """)
    @JsonProperty("gpp_sid")
    private List<Integer> gppSectionIds;

    @Schema(description = """
        US隐私字符串

        美国隐私法规相关的同意字符串。
        """, example = "1---")
    @JsonProperty("us_privacy")
    private String usPrivacy;

    @Schema(description = "扩展字段")
    @JsonProperty("ext")
    private Object ext;

    public Integer getCoppa() {
      return coppa;
    }

    public void setCoppa(Integer coppa) {
      this.coppa = coppa;
    }

    public Integer getGdpr() {
      return gdpr;
    }

    public void setGdpr(Integer gdpr) {
      this.gdpr = gdpr;
    }

    public String getGpp() {
      return gpp;
    }

    public void setGpp(String gpp) {
      this.gpp = gpp;
    }

    public List<Integer> getGppSectionIds() {
      return gppSectionIds;
    }

    public void setGppSectionIds(List<Integer> gppSectionIds) {
      this.gppSectionIds = gppSectionIds;
    }

    public String getUsPrivacy() {
      return usPrivacy;
    }

    public void setUsPrivacy(String usPrivacy) {
      this.usPrivacy = usPrivacy;
    }

    public Object getExt() {
      return ext;
    }

    public void setExt(Object ext) {
      this.ext = ext;
    }
  }

  @Schema(description = """
      限制对象（Restrictions）

      包含额外的竞价限制和规则。
      """)
  public static class Restrictions {
    @Schema(description = "扩展字段")
    @JsonProperty("ext")
    private Object ext;

    public Object getExt() {
      return ext;
    }

    public void setExt(Object ext) {
      this.ext = ext;
    }
  }
}
