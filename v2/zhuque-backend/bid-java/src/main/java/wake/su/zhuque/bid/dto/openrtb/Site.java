package wake.su.zhuque.bid.dto.openrtb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * OpenRTB 2.6 Site Object
 *
 * <p>网站信息对象（Web流量）
 */
@Schema(
    description =
        """
        OpenRTB 2.6 站点信息对象（Site）

        当广告位在网页中时使用，包含网站的相关信息。
        与app字段互斥（Web流量使用site，App流量使用app）。

        **常用字段**：
        - id: 站点ID
        - domain: 站点域名
        - page: 当前页面URL
        - cat: IAB内容类别
        - publisher: 发布商信息
        """)
public class Site {

  @Schema(
      description =
          """
            站点ID

            网站在广告交换平台中的唯一标识符。
            """,
      example = "site-12345")
  @JsonProperty("id")
  private String id;

  @Schema(
      description =
          """
            站点名称

            网站的名称。
            """,
      example = "Example News")
  @JsonProperty("name")
  private String name;

  @Schema(
      description =
          """
            站点域名

            网站的主域名，不含协议和路径。
            """,
      example = "example.com")
  @JsonProperty("domain")
  private String domain;

  @Schema(
      description =
          """
            页面URL

            当前广告位所在页面的完整URL。
            """,
      example = "https://example.com/news/article-123")
  @JsonProperty("page")
  private String page;

  @Schema(
      description =
          """
            引用URL（Referrer）

            用户访问当前页面的来源URL。
            """)
  @JsonProperty("ref")
  private String referrer;

  @Schema(
      description =
          """
            搜索关键词

            如果流量来自搜索引擎，这是用户的搜索查询。
            """)
  @JsonProperty("search")
  private String search;

  @Schema(
      description =
          """
            IAB内容类别（Content Categories）

            使用IAB OpenRTB类别体系对站点内容进行分类。
            如：["IAB3-1", "IAB9"]（商业、新闻）
            """,
      example = "[\"IAB3-1\", \"IAB9\"]")
  @JsonProperty("cat")
  private List<String> categories;

  @Schema(
      description =
          """
            章节类别（Section Categories）

            页面所属的章节类别。
            """)
  @JsonProperty("sectioncat")
  private List<String> sectionCategories;

  @Schema(
      description =
          """
            页面类别（Page Categories）

            当前页面的类别。
            """)
  @JsonProperty("pagecat")
  private List<String> pageCategories;

  @Schema(
      description =
          """
            关键词

            站点的关键词，用于定向。
            """)
  @JsonProperty("keywords")
  private String keywords;

  @Schema(
      description =
          """
            发布商信息

            站点的发布商/所有者信息。
            """)
  @JsonProperty("publisher")
  private Publisher publisher;

  @Schema(
      description =
          """
            内容信息

            页面的内容详情。
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

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public String getPage() {
    return page;
  }

  public void setPage(String page) {
    this.page = page;
  }

  public String getReferrer() {
    return referrer;
  }

  public void setReferrer(String referrer) {
    this.referrer = referrer;
  }

  public String getSearch() {
    return search;
  }

  public void setSearch(String search) {
    this.search = search;
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

  public Publisher getPublisher() {
    return publisher;
  }

  public void setPublisher(Publisher publisher) {
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

  @Schema(
      description =
          """
        发布商对象（Publisher）

        描述站点发布商的信息。
        """)
  public static class Publisher {
    @Schema(description = "发布商ID", example = "pub-12345")
    @JsonProperty("id")
    private String id;

    @Schema(description = "发布商名称", example = "Example Publisher Inc.")
    @JsonProperty("name")
    private String name;

    @Schema(
        description =
            """
                发布商域名

                发布商的主域名。
                """,
        example = "publisher.com")
    @JsonProperty("domain")
    private String domain;

    @Schema(
        description =
            """
                IAB内容类别

                发布商的内容类别。
                """)
    @JsonProperty("cat")
    private List<String> categories;

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
  }
}
