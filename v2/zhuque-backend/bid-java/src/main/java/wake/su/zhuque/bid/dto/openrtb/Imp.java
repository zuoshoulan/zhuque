package wake.su.zhuque.bid.dto.openrtb;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * OpenRTB 2.6 Impression Object
 *
 * <p>展示机会对象，描述一个可竞价广告位 必需字段：id，且至少包含一个广告位类型（banner/video/audio/native）
 */
@Schema(
    description =
        """
        OpenRTB 2.6 展示机会对象（Impression）

        描述一个可竞价的广告位，每个对象代表一个展示机会。

        **必需字段**：
        - id: 展示机会的唯一标识符
        - 且必须包含以下至少一个广告位类型：
          - banner: 横幅广告位
          - video: 视频广告位
          - audio: 音频广告位
          - native: 原生广告位

        **竞价相关字段**：
        - bidfloor: 底价，低于此价格不应出价
        - bidfloorcur: 底价货币（默认USD）
        """)
public class Imp {

  @Schema(
      description = "展示机会的唯一标识符",
      example = "imp-001",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  private String id;

  @Schema(
      description =
          """
            Banner（横幅）广告位信息

            包含横幅广告的尺寸、位置等属性。
            常见字段：
            - w: 宽度（像素）
            - h: 高度（像素）
            - pos: 位置（1=首屏, 2=次屏）
            - mimes: 支持的MIME类型
            """)
  @JsonProperty("banner")
  private Banner banner;

  @Schema(
      description =
          """
            视频广告位信息

            包含视频广告的相关属性。
            常见字段：
            - mimes: 支持的MIME类型（必需）
            - w, h: 视频尺寸
            - linearity: 线性类型（1=线性, 2=非线性）
            - startdelay: 开始延迟（0=前贴片, -1=中贴片）
            - placement: 放置类型（1=流内, 2=插屏）
            - minduration/maxduration: 时长限制
            """)
  @JsonProperty("video")
  private Video video;

  @Schema(
      description =
          """
            音频广告位信息

            包含音频广告的相关属性。
            常见字段：
            - mimes: 支持的MIME类型（必需）
            - minduration/maxduration: 时长限制
            - sequence: 播放顺序
            """)
  @JsonProperty("audio")
  private Audio audio;

  @Schema(
      description =
          """
            原生广告位信息

            包含原生广告的请求规范。
            常见字段：
            - request: 原生广告请求JSON字符串（必需）
            - ver: 原生API版本
            """)
  @JsonProperty("native")
  private NativeX nativeX;

  @Schema(
      description =
          """
            底价（Bid Floor）

            此广告位的最低出价，以CPM为单位。
            如果DSP无法达到此价格，应放弃竞价。
            """,
      example = "0.50")
  @JsonProperty("bidfloor")
  private BigDecimal bidFloor;

  @Schema(
      description =
          """
            底价货币

            bidfloor字段的货币代码，使用ISO-4217标准。
            默认为USD。
            """,
      example = "CNY")
  @JsonProperty("bidfloorcur")
  private String bidFloorCurrency;

  @Schema(
      description =
          """
            HTTPS标志

            指示广告位是否需要HTTPS安全的创意素材。
            - 0: HTTP也可接受
            - 1: 必须使用HTTPS
            """,
      example = "1")
  @JsonProperty("secure")
  private Integer secure;

  @Schema(
      description =
          """
            私有市场（Private Marketplace）

            包含私有交易相关的信息。
            """)
  @JsonProperty("pmp")
  private Pmp pmp;

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

  public Banner getBanner() {
    return banner;
  }

  public void setBanner(Banner banner) {
    this.banner = banner;
  }

  public Video getVideo() {
    return video;
  }

  public void setVideo(Video video) {
    this.video = video;
  }

  public Audio getAudio() {
    return audio;
  }

  public void setAudio(Audio audio) {
    this.audio = audio;
  }

  public NativeX getNativeX() {
    return nativeX;
  }

  public void setNativeX(NativeX nativeX) {
    this.nativeX = nativeX;
  }

  public BigDecimal getBidFloor() {
    return bidFloor;
  }

  public void setBidFloor(BigDecimal bidFloor) {
    this.bidFloor = bidFloor;
  }

  public String getBidFloorCurrency() {
    return bidFloorCurrency;
  }

  public void setBidFloorCurrency(String bidFloorCurrency) {
    this.bidFloorCurrency = bidFloorCurrency;
  }

  public Integer getSecure() {
    return secure;
  }

  public void setSecure(Integer secure) {
    this.secure = secure;
  }

  public Pmp getPmp() {
    return pmp;
  }

  public void setPmp(Pmp pmp) {
    this.pmp = pmp;
  }

  public Object getExt() {
    return ext;
  }

  public void setExt(Object ext) {
    this.ext = ext;
  }

  // ==================== 内部类定义 ====================

  @Schema(
      description =
          """
        Banner广告位

        横幅广告是最常见的广告形式，通常显示在网页的顶部、底部或侧边。
        """)
  public static class Banner {
    @Schema(description = "宽度（像素）", example = "320")
    @JsonProperty("w")
    private Integer width;

    @Schema(description = "高度（像素）", example = "50")
    @JsonProperty("h")
    private Integer height;

    @Schema(
        description =
            """
                最大宽度（像素）

                与w配合使用，表示可接受宽度的范围。
                """,
        example = "320")
    @JsonProperty("wmax")
    private Integer widthMax;

    @Schema(
        description =
            """
                最大高度（像素）

                与h配合使用，表示可接受高度的围。
                """,
        example = "50")
    @JsonProperty("hmax")
    private Integer heightMax;

    @Schema(
        description =
            """
                广告位置

                - 0: 未知
                - 1: 首屏（Above the Fold）
                - 2: 次屏（Below the Fold）
                """,
        example = "1",
        allowableValues = {"0", "1", "2"})
    @JsonProperty("pos")
    private Integer pos;

    @Schema(
        description =
            """
                横幅类型

                - 1: 当屏横幅
                - 2: 可展开/扩展的横幅
                """)
    @JsonProperty("btype")
    private List<Integer> bannerType;

    @Schema(
        description =
            """
                支持的MIME类型

                指定可以展示的创意格式。
                如：["image/jpeg", "image/png", "image/gif"]
                """)
    @JsonProperty("mimes")
    private List<String> mimes;

    @Schema(
        description =
            """
                顶层框架标志

                指示广告位是否在顶层框架中。
                - 0: 在iframe内
                - 1: 在顶层框架
                """,
        example = "1")
    @JsonProperty("topframe")
    private Integer topFrame;

    @Schema(
        description =
            """
                展开方向

                指定可扩展横幅的展开方向。
                """)
    @JsonProperty("expdir")
    private List<Integer> expandDirection;

    @Schema(
        description =
            """
                支持的API框架

                - 1: VPAID 1.0
                - 2: VPAID 2.0
                - 3: MRAID-1
                - 5: MRAID-2
                - 6: MRAID-3
                """)
    @JsonProperty("api")
    private List<Integer> apiFrameworks;

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

    public Integer getWidthMax() {
      return widthMax;
    }

    public void setWidthMax(Integer widthMax) {
      this.widthMax = widthMax;
    }

    public Integer getHeightMax() {
      return heightMax;
    }

    public void setHeightMax(Integer heightMax) {
      this.heightMax = heightMax;
    }

    public Integer getPos() {
      return pos;
    }

    public void setPos(Integer pos) {
      this.pos = pos;
    }

    public List<Integer> getBannerType() {
      return bannerType;
    }

    public void setBannerType(List<Integer> bannerType) {
      this.bannerType = bannerType;
    }

    public List<String> getMimes() {
      return mimes;
    }

    public void setMimes(List<String> mimes) {
      this.mimes = mimes;
    }

    public Integer getTopFrame() {
      return topFrame;
    }

    public void setTopFrame(Integer topFrame) {
      this.topFrame = topFrame;
    }

    public List<Integer> getExpandDirection() {
      return expandDirection;
    }

    public void setExpandDirection(List<Integer> expandDirection) {
      this.expandDirection = expandDirection;
    }

    public List<Integer> getApiFrameworks() {
      return apiFrameworks;
    }

    public void setApiFrameworks(List<Integer> apiFrameworks) {
      this.apiFrameworks = apiFrameworks;
    }
  }

  @Schema(
      description =
          """
        视频广告位

        用于视频流媒体中的视频广告，如前贴片、中贴片、后贴片广告。
        """)
  public static class Video {
    @Schema(
        description =
            """
                支持的MIME类型（必需）

                指定支持的视频格式。
                如：["video/mp4", "video/x-flv"]
                """,
        requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("mimes")
    private List<String> mimes;

    @Schema(description = "视频宽度（像素）", example = "640")
    @JsonProperty("w")
    private Integer width;

    @Schema(description = "视频高度（像素）", example = "480")
    @JsonProperty("h")
    private Integer height;

    @Schema(
        description =
            """
                线性类型

                - 1: 线性广告（会暂停内容播放）
                - 2: 非线性广告（与内容同时播放，如叠加广告）
                """,
        example = "1")
    @JsonProperty("linearity")
    private Integer linearity;

    @Schema(description = "最小时长（秒）", example = "15")
    @JsonProperty("minduration")
    private Integer minDuration;

    @Schema(description = "最大时长（秒）", example = "30")
    @JsonProperty("maxduration")
    private Integer maxDuration;

    @Schema(
        description =
            """
                开始延迟

                - 0: 前贴片（播放前）
                - -1: 中贴片（播放中）
                - >0: 后贴片（播放后多少秒）
                """,
        example = "0")
    @JsonProperty("startdelay")
    private Integer startDelay;

    @Schema(
        description =
            """
                放置类型

                - 1: 流内（In-Stream），如视频前贴片
                - 2: 插屏（Interstitial）
                - 3: 悬停/浮层
                - 4: 播放器外
                """,
        example = "1")
    @JsonProperty("placement")
    private Integer placement;

    @Schema(
        description =
            """
                是否可跳过

                - 0: 不可跳过
                - 1: 可跳过
                """,
        example = "1")
    @JsonProperty("skip")
    private Integer skip;

    @Schema(
        description =
            """
                播放方法

                - 1: 自动播放有声
                - 2: 自动播放静音
                - 3: 点击播放
                - 4: 鼠标悬停播放
                """,
        example = "2")
    @JsonProperty("playbackend")
    private Integer playbackMethod;

    @Schema(
        description =
            """
                交付类型

                - 1: 流式传输
                - 2: 渐进式下载
                """)
    @JsonProperty("delivery")
    private List<Integer> delivery;

    @Schema(
        description =
            """
                伴随广告（Companion Ads）

                与视频广告同时展示的横幅广告。
                """)
    @JsonProperty("companionad")
    private List<Banner> companionAds;

    public List<String> getMimes() {
      return mimes;
    }

    public void setMimes(List<String> mimes) {
      this.mimes = mimes;
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

    public Integer getLinearity() {
      return linearity;
    }

    public void setLinearity(Integer linearity) {
      this.linearity = linearity;
    }

    public Integer getMinDuration() {
      return minDuration;
    }

    public void setMinDuration(Integer minDuration) {
      this.minDuration = minDuration;
    }

    public Integer getMaxDuration() {
      return maxDuration;
    }

    public void setMaxDuration(Integer maxDuration) {
      this.maxDuration = maxDuration;
    }

    public Integer getStartDelay() {
      return startDelay;
    }

    public void setStartDelay(Integer startDelay) {
      this.startDelay = startDelay;
    }

    public Integer getPlacement() {
      return placement;
    }

    public void setPlacement(Integer placement) {
      this.placement = placement;
    }

    public Integer getSkip() {
      return skip;
    }

    public void setSkip(Integer skip) {
      this.skip = skip;
    }

    public Integer getPlaybackMethod() {
      return playbackMethod;
    }

    public void setPlaybackMethod(Integer playbackMethod) {
      this.playbackMethod = playbackMethod;
    }

    public List<Integer> getDelivery() {
      return delivery;
    }

    public void setDelivery(List<Integer> delivery) {
      this.delivery = delivery;
    }

    public List<Banner> getCompanionAds() {
      return companionAds;
    }

    public void setCompanionAds(List<Banner> companionAds) {
      this.companionAds = companionAds;
    }
  }

  @Schema(
      description =
          """
        音频广告位

        用于音频流媒体中的音频广告，如音乐、播客中的广告。
        """)
  public static class Audio {
    @Schema(
        description =
            """
                支持的MIME类型（必需）

                指定支持的音频格式。
                如：["audio/mp3", "audio/aac"]
                """,
        requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("mimes")
    private List<String> mimes;

    @Schema(description = "最小时长（秒）", example = "15")
    @JsonProperty("minduration")
    private Integer minDuration;

    @Schema(description = "最大时长（秒）", example = "30")
    @JsonProperty("maxduration")
    private Integer maxDuration;

    @Schema(
        description =
            """
                播放序列

                表示广告在广告组中的位置。
                """,
        example = "1")
    @JsonProperty("sequence")
    private Integer sequence;

    public List<String> getMimes() {
      return mimes;
    }

    public void setMimes(List<String> mimes) {
      this.mimes = mimes;
    }

    public Integer getMinDuration() {
      return minDuration;
    }

    public void setMinDuration(Integer minDuration) {
      this.minDuration = minDuration;
    }

    public Integer getMaxDuration() {
      return maxDuration;
    }

    public void setMaxDuration(Integer maxDuration) {
      this.maxDuration = maxDuration;
    }

    public Integer getSequence() {
      return sequence;
    }

    public void setSequence(Integer sequence) {
      this.sequence = sequence;
    }
  }

  @Schema(
      description =
          """
        原生广告位

        原生广告的广告素材样式与页面内容融为一体，
        需要返回具体的组件数据（标题、图片、描述等）而非HTML。
        """)
  public static class NativeX {
    @Schema(
        description =
            """
                原生广告请求（必需）

                一个JSON字符串，包含需要返回的组件规范。
                遵循IAB Native Advertising Specification。
                """,
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "{\"assets\":[{\"id\":1,\"required\":1,\"title\":{\"text\":25}}]}")
    @JsonProperty("request")
    private String request;

    @Schema(
        description =
            """
                原生API版本

                指定使用的原生广告规范版本。
                """,
        example = "1.2")
    @JsonProperty("ver")
    private String version;

    @Schema(
        description =
            """
                支持的API框架

                - 5: MRAID-1
                - 6: MRAID-2
                - 7: MRAID-3
                """)
    @JsonProperty("api")
    private List<Integer> apiFrameworks;

    public String getRequest() {
      return request;
    }

    public void setRequest(String request) {
      this.request = request;
    }

    public String getVersion() {
      return version;
    }

    public void setVersion(String version) {
      this.version = version;
    }

    public List<Integer> getApiFrameworks() {
      return apiFrameworks;
    }

    public void setApiFrameworks(List<Integer> apiFrameworks) {
      this.apiFrameworks = apiFrameworks;
    }
  }

  @Schema(
      description =
          """
        私有市场对象

        用于处理私有交易（PMP/Programmatic Guaranteed）。
        """)
  public static class Pmp {
    @Schema(
        description =
            """
                私有拍卖标志

                - 0: 非私有拍卖
                - 1: 私有拍卖，只有指定的买家可以参与
                """,
        example = "1")
    @JsonProperty("private_auction")
    private Integer privateAuction;

    @Schema(
        description =
            """
                交易列表

                包含可用的私有交易（Deals）。
                """)
    @JsonProperty("deals")
    private List<Deal> deals;

    public Integer getPrivateAuction() {
      return privateAuction;
    }

    public void setPrivateAuction(Integer privateAuction) {
      this.privateAuction = privateAuction;
    }

    public List<Deal> getDeals() {
      return deals;
    }

    public void setDeals(List<Deal> deals) {
      this.deals = deals;
    }
  }

  @Schema(
      description =
          """
        交易对象（Deal）

        私有市场中的交易配置。
        """)
  public static class Deal {
    @Schema(
        description = "交易唯一标识符",
        example = "deal-123",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("id")
    private String id;

    @Schema(
        description =
            """
                交易底价

                此交易的最低出价，以CPM为单位。
                """,
        example = "2.50")
    @JsonProperty("bidfloor")
    private BigDecimal bidFloor;

    @Schema(
        description =
            """
                底价货币

                bidfloor字段的货币代码。
                """,
        example = "USD")
    @JsonProperty("bidfloorcur")
    private String bidFloorCurrency;

    @Schema(
        description =
            """
                拍卖类型

                - 1: 首价拍卖
                - 2: 次价拍卖
                """,
        example = "2")
    @JsonProperty("at")
    private Integer auctionType;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public BigDecimal getBidFloor() {
      return bidFloor;
    }

    public void setBidFloor(BigDecimal bidFloor) {
      this.bidFloor = bidFloor;
    }

    public String getBidFloorCurrency() {
      return bidFloorCurrency;
    }

    public void setBidFloorCurrency(String bidFloorCurrency) {
      this.bidFloorCurrency = bidFloorCurrency;
    }

    public Integer getAuctionType() {
      return auctionType;
    }

    public void setAuctionType(Integer auctionType) {
      this.auctionType = auctionType;
    }
  }
}
