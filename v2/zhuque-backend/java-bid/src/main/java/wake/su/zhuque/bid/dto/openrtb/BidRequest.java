package wake.su.zhuque.bid.dto.openrtb;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * OpenRTB 2.6 Bid Request
 */
public class BidRequest {

    @JsonProperty("id")
    private String id;

    @JsonProperty("imp")
    private List<Imp> imp;

    @JsonProperty("user")
    private User user;

    @JsonProperty("device")
    private Device device;

    @JsonProperty("site")
    private Site site;

    @JsonProperty("app")
    private App app;

    @JsonProperty("test")
    private Integer test;

    @JsonProperty("at")
    private Integer auctionType;

    @JsonProperty("tmax")
    private Integer timeout;

    @JsonProperty("cur")
    private List<String> currencies;

    @JsonProperty("bcat")
    private List<String> blockedCategories;

    @JsonProperty("badv")
    private List<String> blockedAdvertisers;

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
