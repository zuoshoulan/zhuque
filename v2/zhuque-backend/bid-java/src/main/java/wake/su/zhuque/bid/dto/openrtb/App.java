package wake.su.zhuque.bid.dto.openrtb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * OpenRTB 2.6 App Object
 *
 * <p>应用信息对象（App流量）
 */
@Schema(
    description =
        """
        OpenRTB 2.6 应用信息对象（App）

        当广告位在移动应用中时使用，包含应用的相关信息。
        与site字段互斥（Web流量使用site，App流量使用app）。

        **常用字段**：
        - id: 应用ID
        - bundle: 应用包名（如com.example.app）
        - ver: 应用版本
        - publisher: 发布商信息
        """)
public class App {

  @Schema(
      description =
          """
            应用ID

            应用在广告交换平台中的唯一标识符。
            """,
      example = "app-12345")
  @JsonProperty("id")
  private String id;

  @Schema(
      description =
          """
            应用名称

            应用的显示名称。
            """,
      example = "Example Game")
  @JsonProperty("name")
  private String name;

  @Schema(
      description =
          """
            应用包名（Bundle）

            应用的唯一包标识符。
            - iOS: Bundle ID（如com.example.app）
            - Android: Package Name（如com.example.app）
            """,
      example = "com.example.game")
  @JsonProperty("bundle")
  private String bundle;

  @Schema(
      description =
          """
            应用域名

            应用关联的域名。
            """)
  @JsonProperty("domain")
  private String domain;

  @Schema(
      description =
          """
            应用商店URL

            应用在应用商店的详情页面URL。
            """)
  @JsonProperty("storeurl")
  private String storeUrl;

  @Schema(
      description =
          """
            应用版本（Version）

            应用的版本号。
            """,
      example = "1.2.3")
  @JsonProperty("ver")
  private String version;

  @Schema(
      description =
          """
            IAB内容类别（Content Categories）

            使用IAB OpenRTB类别体系对应用内容进行分类。
            """)
  @JsonProperty("cat")
  private List<String> categories;

  @Schema(
      description =
          """
            章节类别（Section Categories）

            应用所属的章节类别。
            """)
  @JsonProperty("sectioncat")
  private List<String> sectionCategories;

  @Schema(
      description =
          """
            页面类别（Page Categories）

            应用内页面的类别。
            """)
  @JsonProperty("pagecat")
  private List<String> pageCategories;

  @Schema(
      description =
          """
            关键词

            应用的关键词，用于定向。
            """)
  @JsonProperty("keywords")
  private String keywords;

  @Schema(
      description =
          """
            隐私政策（Privacy Policy）

            指示应用是否有隐私政策。
            - 0: 无隐私政策
            - 1: 有隐私政策
            """,
      example = "1")
  @JsonProperty("privacypolicy")
  private Integer privacyPolicy;

  @Schema(
      description =
          """
            付费应用（Paid App）

            指示应用是否为付费应用。
            - 0: 免费应用
            - 1: 付费应用
            """,
      example = "0")
  @JsonProperty("paid")
  private Integer paid;

  @Schema(
      description =
          """
            发布商信息

            应用的发布商/开发者信息。
            """)
  @JsonProperty("publisher")
  private Site.Publisher publisher;

  @Schema(
      description =
          """
            内容信息

            应用的内容详情。
            """)
  @JsonProperty("content")
  private Object content;

  @Schema(
      description =
          """
            扩展字段

            用于自定义数据的扩展对象。
            """)
  @JsonProperty("ext")
  private Object ext;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getBundle() {
    return bundle;
  }

  public void setBundle(String bundle) {
    this.bundle = bundle;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public String getStoreUrl() {
    return storeUrl;
  }

  public void setStoreUrl(String storeUrl) {
    this.storeUrl = storeUrl;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public List<String> getCategories() {
    return categories;
  }

  public void setCategories(List<String> categories) {
    this.categories = categories;
  }

  public List<String> getSectionCategories() {
    return sectionCategories;
  }

  public void setSectionCategories(List<String> sectionCategories) {
    this.sectionCategories = sectionCategories;
  }

  public List<String> getPageCategories() {
    return pageCategories;
  }

  public void setPageCategories(List<String> pageCategories) {
    this.pageCategories = pageCategories;
  }

  public String getKeywords() {
    return keywords;
  }

  public void setKeywords(String keywords) {
    this.keywords = keywords;
  }

  public Integer getPrivacyPolicy() {
    return privacyPolicy;
  }

  public void setPrivacyPolicy(Integer privacyPolicy) {
    this.privacyPolicy = privacyPolicy;
  }

  public Integer getPaid() {
    return paid;
  }

  public void setPaid(Integer paid) {
    this.paid = paid;
  }

  public Site.Publisher getPublisher() {
    return publisher;
  }

  public void setPublisher(Site.Publisher publisher) {
    this.publisher = publisher;
  }

  public Object getContent() {
    return content;
  }

  public void setContent(Object content) {
    this.content = content;
  }

  public Object getExt() {
    return ext;
  }

  public void setExt(Object ext) {
    this.ext = ext;
  }
}
