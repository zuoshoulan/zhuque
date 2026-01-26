package wake.su.zhuque.bid.dto.openrtb;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

/**
 * OpenRTB 2.6 Bid Object
 * <p>
 * 竞价响应对象，表示一次出价
 * </p>
 */
@Schema(
    description = """
        OpenRTB 2.6 竞价对象（Bid）

        表示对单个展示机会的出价，包含出价价格和广告创意信息。

        **必需字段**：
        - id: 竞价的唯一标识符
        - impid: 所竞价展示机会的ID（必须与请求中的imp.id匹配）
        - price: 出价价格（微美元或CPM）

        **价格说明**：
        OpenRTB标准使用微美元（Micros）作为单位：
        - $1.00 = 1,000,000 微美元
        - $0.75 = 750,000 微美元

        如果需要使用CPM（分）作为单位，请在文档中说明。
        """
)
public class Bid {

    @Schema(
        description = """
            竞价ID（Bid ID）

            此竞价的唯一标识符，用于日志追踪和调试。
            """,
        example = "bid-001",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("id")
    private String id;

    @Schema(
        description = """
            展示机会ID（Impression ID）

            必须与请求中的imp.id匹配，指定此竞价针对哪个广告位。
            """,
        example = "imp-001",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("impid")
    private String impId;

    @Schema(
        description = """
            出价价格（Price）

            DSP的出价，单位取决于实现：
            - 标准OpenRTB：微美元（Micros），$1.00 = 1,000,000
            - 某些实现：CPM分，需要与SSP协商

            本实现使用CPM分作为单位。
            """,
        example = "125"
    )
    @JsonProperty("price")
    private BigDecimal price;

    @Schema(
        description = """
            广告ID（Ad ID）

            广告主或DSP侧的广告标识符。
            """,
        example = "ad-001"
    )
    @JsonProperty("adid")
    private String adId;

    @Schema(
        description = """
            获胜通知URL（Win Notice URL）

            当此竞价获胜时，SSP会调用此URL通知DSP。
            可以使用宏替换，如${AUCTION_PRICE}获取最终成交价。
            """,
        example = "https://ad.zhuque.com/win/${AUCTION_PRICE}"
    )
    @JsonProperty("nurl")
    private String winNoticeUrl;

    @Schema(
        description = """
            计费通知URL（Billing Notice URL）

            用于确认广告已展示/计费的URL。
            """)
    @JsonProperty("burl")
    private String billingUrl;

    @Schema(
        description = """
            失败通知URL（Loss Notice URL）

            当此竞价失败时调用，用于传递竞价失败信息。
            """)
    @JsonProperty("lurl")
    private String lossUrl;

    @Schema(
        description = """
            广告主域名列表（Advertiser Domains）

            广告主的根域名列表，用于品牌安全和白名单过滤。
            如：["advertiser.com", "brand.com"]
            """,
        example = "[\"advertiser.com\"]"
    )
    @JsonProperty("adomain")
    private List<String> adDomain;

    @Schema(
        description = """
            应用包名（Bundle）

            App流量中，点击广告后跳转的应用包名。
            """)
    @JsonProperty("bundle")
    private String bundle;

    @Schema(
        description = """
            预览图片URL（Image URL）

            广告创意的预览图片URL，用于审核或预览。
            """)
    @JsonProperty("iurl")
    private String imageUrl;

    @Schema(
        description = """
            创意ID（Creative ID）

            广告创意的唯一标识符。
            """,
        example = "creative-001"
    )
    @JsonProperty("crid")
    private String creativeId;

    @Schema(
        description = """
            广告活动ID（Campaign ID）

            广告活动/计划的标识符。
            """,
        example = "campaign-001"
    )
    @JsonProperty("cid")
    private String campaignId;

    @Schema(
        description = """
            IAB内容类别列表

            广告的内容类别，用于品牌安全过滤。
            """)
    @JsonProperty("cat")
    private List<String> categories;

    @Schema(
        description = """
            创意属性列表

            指定创意的特殊属性或限制：
            - 1: 音频（用户自发动作）
            - 2: 用户自发（展开）
            - 3: 鼠标悬停效果
            - 5: 自动播放音频
            - 6: 展开效果
            """)
    @JsonProperty("attr")
    private List<Integer> attributes;

    @Schema(
        description = """
            API框架列表

            创意支持的API框架：
            - 1: VPAID 1.0
            - 2: VPAID 2.0
            - 3: MRAID-1
            - 5: MRAID-2
            - 6: MRAID-3
            """)
    @JsonProperty("api")
    private List<Integer> apiFrameworks;

    @Schema(
        description = """
            广告宽度（像素）

            广告创意的宽度。
            """,
        example = "320"
    )
    @JsonProperty("w")
    private Integer width;

    @Schema(
        description = """
            广告高度（像素）

            广告创意的高度。
            """,
        example = "50"
    )
    @JsonProperty("h")
    private Integer height;

    @Schema(
        description = """
            视频时长（秒）

            视频/音频广告的持续时间。
            """)
    @JsonProperty("dur")
    private Integer duration;

    @Schema(
        description = """
            交易ID（Deal ID）

            如果此竞价属于私有交易（PMP），指定交易ID。
            """)
    @JsonProperty("dealid")
    private String dealId;

    @Schema(
        description = """
            广告素材 markup（Ad Markup）

            广告创意的实际内容，格式取决于广告类型：
            - Banner: HTML片段
            - Video: VAST XML文档URL
            - Native: JSON格式的组件数据
            """,
        example = "<a href='https://landing-page.com'><img src='https://creative.com/banner.jpg'/></a>"
    )
    @JsonProperty("adm")
    private String adm;

    @Schema(
        description = """
            扩展字段

            用于自定义数据的扩展对象。
            OpenRTB 2.6新增扩展：
            - ext.prebid: Prebid特定扩展
            - ext.segbdy: 分段主体标识符
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

    public String getImpId() {
        return impId;
    }

    public void setImpId(String impId) {
        this.impId = impId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public String getWinNoticeUrl() {
        return winNoticeUrl;
    }

    public void setWinNoticeUrl(String winNoticeUrl) {
        this.winNoticeUrl = winNoticeUrl;
    }

    public String getBillingUrl() {
        return billingUrl;
    }

    public void setBillingUrl(String billingUrl) {
        this.billingUrl = billingUrl;
    }

    public String getLossUrl() {
        return lossUrl;
    }

    public void setLossUrl(String lossUrl) {
        this.lossUrl = lossUrl;
    }

    public List<String> getAdDomain() {
        return adDomain;
    }

    public void setAdDomain(List<String> adDomain) {
        this.adDomain = adDomain;
    }

    public String getBundle() {
        return bundle;
    }

    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCreativeId() {
        return creativeId;
    }

    public void setCreativeId(String creativeId) {
        this.creativeId = creativeId;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<Integer> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Integer> attributes) {
        this.attributes = attributes;
    }

    public List<Integer> getApiFrameworks() {
        return apiFrameworks;
    }

    public void setApiFrameworks(List<Integer> apiFrameworks) {
        this.apiFrameworks = apiFrameworks;
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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getDealId() {
        return dealId;
    }

    public void setDealId(String dealId) {
        this.dealId = dealId;
    }

    public String getAdm() {
        return adm;
    }

    public void setAdm(String adm) {
        this.adm = adm;
    }

    public Object getExt() {
        return ext;
    }

    public void setExt(Object ext) {
        this.ext = ext;
    }
}
