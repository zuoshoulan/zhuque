package wake.su.zhuque.bid.dto.openrtb;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * OpenRTB 2.6 Device Object
 *
 * <p>设备信息对象，包含用户设备的详细属性
 */
@Schema(
    description =
        """
        OpenRTB 2.6 设备信息对象（Device）

        包含用户设备的相关信息，用于设备定向和创意适配。

        **常用字段**：
        - ua: 用户代理字符串（用于识别浏览器和设备）
        - ip: IP地址（用于地理位置定位）
        - devicetype: 设备类型
        - os: 操作系统
        - geo: 地理位置信息
        """)
public class Device {

  @Schema(
      description =
          """
            用户代理字符串（User Agent）

            用于识别浏览器类型、版本和操作系统。
            这是设备识别的重要字段。
            """,
      example = "Mozilla/5.0 (Linux; Android 12; SM-G991B) AppleWebKit/537.36")
  @JsonProperty("ua")
  private String userAgent;

  @Schema(
      description =
          """
            IP地址（IPv4）

            用户的IPv4地址，用于地理位置定位和反欺诈。
            注意：隐私合规要求下，IP可能会被hash或脱敏。
            """,
      example = "210.73.204.1")
  @JsonProperty("ip")
  private String ip;

  @Schema(
      description =
          """
            IPv6地址

            用户的IPv6地址（如果适用）。
            """,
      example = "2001:db8::1")
  @JsonProperty("ipv6")
  private String ipv6;

  @Schema(
      description =
          """
            设备类型

            - 1: 手机/手持设备（Mobile/Tablet）
            - 2: 个人电脑（PC）
            - 3: 平板（Tablet）
            - 4: 联网电视（CTV）
            - 5: 机顶盒（Set-Top Box）
            - 6: 家庭助理（Home Assistant）
            - 7: 游戏机（Game Console）
            """,
      example = "1",
      allowableValues = {"1", "2", "3", "4", "5", "6", "7"})
  @JsonProperty("devicetype")
  private Integer deviceType;

  @Schema(
      description =
          """
            操作系统

            常见值：iOS, Android, Windows, macOS, Linux, Roku, tvOS, ChromeOS
            """,
      example = "Android")
  @JsonProperty("os")
  private String os;

  @Schema(
      description =
          """
            操作系统版本

            操作系统的版本号，用于定向和创意适配。
            """,
      example = "12")
  @JsonProperty("osv")
  private String osVersion;

  @Schema(
      description =
          """
            设备制造商

            如：Apple, Samsung, Huawei, Xiaomi
            """,
      example = "Samsung")
  @JsonProperty("make")
  private String make;

  @Schema(
      description =
          """
            设备型号

            具体的设备型号，如：iPhone13, SM-G991B
            """,
      example = "SM-G991B")
  @JsonProperty("model")
  private String model;

  @Schema(
      description =
          """
            屏幕宽度（像素）

            设备屏幕的宽度，用于创意适配。
            """,
      example = "360")
  @JsonProperty("w")
  private Integer width;

  @Schema(
      description =
          """
            屏幕高度（像素）

            设备屏幕的高度，用于创意适配。
            """,
      example = "800")
  @JsonProperty("h")
  private Integer height;

  @Schema(
      description =
          """
            屏幕像素密度（PPI）

            每英寸像素数。
            """,
      example = "320")
  @JsonProperty("ppi")
  private Integer ppi;

  @Schema(
      description =
          """
            像素比

            设备独立像素与物理像素的比例。
            - 1.0: 标准屏
            - 2.0: 2x屏（Retina）
            - 3.0: 3x屏
            """,
      example = "2.0")
  @JsonProperty("pxratio")
  private Float pixelRatio;

  @Schema(
      description =
          """
            JavaScript支持

            指示设备是否支持JavaScript。
            - 0: 不支持
            - 1: 支持
            """,
      example = "1")
  @JsonProperty("js")
  private Integer jsSupport;

  @Schema(
      description =
          """
            请勿跟踪（Do Not Track）

            指示用户是否启用了"请勿跟踪"设置。
            - 0: 未启用
            - 1: 启用
            """,
      example = "0")
  @JsonProperty("dnt")
  private Integer doNotTrack;

  @Schema(
      description =
          """
            限制广告跟踪（Limit Ad Tracking）

            指示用户是否限制了广告跟踪（iOS/Android）。
            - 0: 允许跟踪
            - 1: 限制跟踪
            """,
      example = "0")
  @JsonProperty("lmt")
  private Integer limitAdTracking;

  @Schema(
      description =
          """
            移动设备标识符（IFA/IDFA）

            设备的广告标识符：
            - iOS: IDFA (Identifier for Advertisers)
            - Android: GAID (Google Advertising ID)

            注意：隐私法规下可能为空或hash值。
            """,
      example = "AEBE52E7-03EE-455A-B3C4-E57283966239")
  @JsonProperty("ifa")
  private String ifa;

  @Schema(
      description =
          """
            连接类型

            - 0: 未知
            - 1: 以太网
            - 2: WiFi
            - 3: 2G网络
            - 4: 3G网络
            - 5: 4G网络
            - 6: 5G网络（OpenRTB 2.6新增）
            """,
      example = "5")
  @JsonProperty("connectiontype")
  private Integer connectionType;

  @Schema(
      description =
          """
            运营商（Carrier）

            移动网络运营商的名称。
            """,
      example = "China Mobile")
  @JsonProperty("carrier")
  private String carrier;

  @Schema(
      description =
          """
            移动国家代码和网络代码（MCCMNC）

            移动网络标识，格式为MCC-MNC。
            如：460-01（中国移动）
            """,
      example = "460-01")
  @JsonProperty("mccmnc")
  private String mccmnc;

  @Schema(
      description =
          """
            语言（Language）

            设备的语言设置，使用ISO-639-1-alpha-2标准。
            """,
      example = "zh")
  @JsonProperty("language")
  private String language;

  @Schema(
      description =
          """
            地理位置信息

            包含用户的地理位置数据。
            """)
  @JsonProperty("geo")
  private Geo geo;

  @Schema(
      description =
          """
            扩展字段

            用于自定义数据的扩展对象。
            OpenRTB 2.6新增扩展：
            - ext.carrier: 运营商详细信息
            - ext.devicemodel: 设备型号详情
            - ext.ispon: 隐私VPN标志
            - ext.mmdevicetype: MMA设备类型
            """)
  @JsonProperty("ext")
  private Object ext;

  public String getUserAgent() {
    return userAgent;
  }

  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public String getIpv6() {
    return ipv6;
  }

  public void setIpv6(String ipv6) {
    this.ipv6 = ipv6;
  }

  public Integer getDeviceType() {
    return deviceType;
  }

  public void setDeviceType(Integer deviceType) {
    this.deviceType = deviceType;
  }

  public String getOs() {
    return os;
  }

  public void setOs(String os) {
    this.os = os;
  }

  public String getOsVersion() {
    return osVersion;
  }

  public void setOsVersion(String osVersion) {
    this.osVersion = osVersion;
  }

  public String getMake() {
    return make;
  }

  public void setMake(String make) {
    this.make = make;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public Integer getWidth() {
    return width;
  }

  public void setWidth(Integer width) {
    this.width = width;
  }

  public Integer getHeight() {
    return height;
  }

  public void setHeight(Integer height) {
    this.height = height;
  }

  public Integer getPpi() {
    return ppi;
  }

  public void setPpi(Integer ppi) {
    this.ppi = ppi;
  }

  public Float getPixelRatio() {
    return pixelRatio;
  }

  public void setPixelRatio(Float pixelRatio) {
    this.pixelRatio = pixelRatio;
  }

  public Integer getJsSupport() {
    return jsSupport;
  }

  public void setJsSupport(Integer jsSupport) {
    this.jsSupport = jsSupport;
  }

  public Integer getDoNotTrack() {
    return doNotTrack;
  }

  public void setDoNotTrack(Integer doNotTrack) {
    this.doNotTrack = doNotTrack;
  }

  public Integer getLimitAdTracking() {
    return limitAdTracking;
  }

  public void setLimitAdTracking(Integer limitAdTracking) {
    this.limitAdTracking = limitAdTracking;
  }

  public String getIfa() {
    return ifa;
  }

  public void setIfa(String ifa) {
    this.ifa = ifa;
  }

  public Integer getConnectionType() {
    return connectionType;
  }

  public void setConnectionType(Integer connectionType) {
    this.connectionType = connectionType;
  }

  public String getCarrier() {
    return carrier;
  }

  public void setCarrier(String carrier) {
    this.carrier = carrier;
  }

  public String getMccmnc() {
    return mccmnc;
  }

  public void setMccmnc(String mccmnc) {
    this.mccmnc = mccmnc;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public Geo getGeo() {
    return geo;
  }

  public void setGeo(Geo geo) {
    this.geo = geo;
  }

  public Object getExt() {
    return ext;
  }

  public void setExt(Object ext) {
    this.ext = ext;
  }
}
