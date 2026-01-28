package wake.su.zhuque.bid.dto.openrtb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * OpenRTB 2.6 User Object
 *
 * <p>用户信息对象，包含当前用户的相关信息
 */
@Schema(description = """
    OpenRTB 2.6 用户信息对象（User）

    包含当前用户的相关信息，用于用户定向和用户识别。

    **常用字段**：
    - id: 交换侧的用户ID（由SSP生成）
    - buyeruid: DSP侧的用户ID（用于DSP侧的用户识别和重定向）
    - yob: 出生年份（用于年龄定向）
    - gender: 性别
    - geo: 地理位置信息（可能与设备geo不同）
    """)
public class User {

  @Schema(description = """
      用户唯一标识符

      由广告交换平台生成的用户ID，
      用于在本次请求中标识用户。
      """, example = "user-001")
  @JsonProperty("id")
  private String id;

  @Schema(description = """
      买方用户ID（Buyer UID）

      由DSP（买方）生成的用户ID，
      用于DSP侧的用户识别、重定向和频率控制。
      这是DSP侧最重要的用户标识符。
      """, example = "dsp-user-12345")
  @JsonProperty("buyeruid")
  private String buyerUid;

  @Schema(description = """
      出生年份（Year of Birth）

      用于年龄定向计算用户年龄段。
      注意：出于隐私考虑，此字段在某些地区可能不可用。
      """, example = "1990")
  @JsonProperty("yob")
  private Integer yearOfBirth;

  @Schema(description = """
      性别

      - M: 男性（Male）
      - F: 女性（Female）
      - O: 其他（Other）
      """, example = "M", allowableValues = { "M", "F", "O" })
  @JsonProperty("gender")
  private String gender;

  @Schema(description = """
      关键词

      逗号分隔的关键词列表，描述用户的兴趣或特征。
      如："travel, luxury, automotive"
      """, example = "travel, sports, technology")
  @JsonProperty("keywords")
  private String keywords;

  @Schema(description = """
      自定义数据

      买方提供的自定义数据，用于传递额外的用户信息。
      """)
  @JsonProperty("customdata")
  private String customData;

  @Schema(description = """
      地理位置信息

      用户注册或自选的地理位置，
      可能与设备geo（基于IP的定位）不同。
      """)
  @JsonProperty("geo")
  private Geo geo;

  @Schema(description = """
      数据段列表

      包含第三方数据提供商的用户分群数据。
      用于高级用户定向。
      """)
  @JsonProperty("data")
  private List<Data> data;

  @Schema(description = """
      扩展字段

      用于自定义数据的扩展对象。
      OpenRTB 2.6新增扩展：
      - ext.eids: 扩展ID列表（ID Bridging）
      - ext.consent: GDPR同意字符串
      - ext.digid: 数字ID
      """)
  @JsonProperty("ext")
  private Object ext;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getBuyerUid() {
    return buyerUid;
  }

  public void setBuyerUid(String buyerUid) {
    this.buyerUid = buyerUid;
  }

  public Integer getYearOfBirth() {
    return yearOfBirth;
  }

  public void setYearOfBirth(Integer yearOfBirth) {
    this.yearOfBirth = yearOfBirth;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getKeywords() {
    return keywords;
  }

  public void setKeywords(String keywords) {
    this.keywords = keywords;
  }

  public String getCustomData() {
    return customData;
  }

  public void setCustomData(String customData) {
    this.customData = customData;
  }

  public Geo getGeo() {
    return geo;
  }

  public void setGeo(Geo geo) {
    this.geo = geo;
  }

  public List<Data> getData() {
    return data;
  }

  public void setData(List<Data> data) {
    this.data = data;
  }

  public Object getExt() {
    return ext;
  }

  public void setExt(Object ext) {
    this.ext = ext;
  }

  @Schema(description = """
      数据提供商对象（Data）

      包含第三方数据提供商提供的用户分群数据。
      """)
  public static class Data {
    @Schema(description = "数据提供商ID", example = "provider-123")
    @JsonProperty("id")
    private String id;

    @Schema(description = "数据提供商名称", example = "Data Provider Inc.")
    @JsonProperty("name")
    private String name;

    @Schema(description = """
        织分段（Segment）列表

        包含用户所属的分群段信息。
        """)
    @JsonProperty("segment")
    private List<Segment> segment;

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

    public List<Segment> getSegment() {
      return segment;
    }

    public void setSegment(List<Segment> segment) {
      this.segment = segment;
    }
  }

  @Schema(description = """
      织分段对象（Segment）

      描述用户所属的具体分群段。
      """)
  public static class Segment {
    @Schema(description = """
        织分段ID（必需）

        分群段的唯一标识符。
        """, example = "seg-001")
    @JsonProperty("id")
    private String id;

    @Schema(description = "织分段名称", example = "Travel Intenders")
    @JsonProperty("name")
    private String name;

    @Schema(description = """
        织分段值

        描述该分群段的具体值或含义。
        """, example = "travel")
    @JsonProperty("value")
    private String value;

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

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }
  }
}
