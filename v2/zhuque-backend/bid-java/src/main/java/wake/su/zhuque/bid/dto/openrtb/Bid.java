package wake.su.zhuque.bid.dto.openrtb;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 * OpenRTB 2.6 Bid Object
 *
 * @author zhuque
 * @version 1.0
 */
public class Bid {

    @JsonProperty("id")
    private String id;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("adid")
    private String adId;

    @JsonProperty("adomain")
    private List<String> adDomain;

    @JsonProperty("bundle")
    private String bundle;

    @JsonProperty("iurl")
    private String imageUrl;

    @JsonProperty("crid")
    private String creativeId;

    @JsonProperty("adm")
    private String adm;

    @JsonProperty("cid")
    private String campaignId;

    @JsonProperty("cat")
    private List<String> categories;

    @JsonProperty("attr")
    private List<Integer> attributes;

    @JsonProperty("api")
    private List<Integer> apiFrameworks;

    @JsonProperty("w")
    private Integer width;

    @JsonProperty("h")
    private Integer height;

    @JsonProperty("dur")
    private Integer duration;

    @JsonProperty("dealid")
    private String dealId;

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getAdm() {
        return adm;
    }

    public void setAdm(String adm) {
        this.adm = adm;
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
}
