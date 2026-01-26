package wake.su.zhuque.bid.context;

import wake.su.zhuque.bid.dto.openrtb.BidRequest;
import wake.su.zhuque.bid.dto.openrtb.Device;
import wake.su.zhuque.bid.dto.openrtb.Geo;
import wake.su.zhuque.bid.dto.openrtb.Imp;
import wake.su.zhuque.bid.dto.openrtb.User;
import wake.su.zhuque.model.entity.RtbAdGroupDO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 竞价上下文对象
 * 贯穿整个竞价流程，携带请求和中间计算结果
 *
 * @author zhuque
 * @version 1.0
 */
public class BidContext {

    /**
     * 原始竞价请求
     */
    private BidRequest request;

    /**
     * 当前处理的展示机会
     */
    private Imp currentImp;

    /**
     * 设备信息
     */
    private Device device;

    /**
     * 地理位置信息
     */
    private Geo geo;

    /**
     * 用户信息
     */
    private User user;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 设备类型
     */
    private Integer deviceType;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 国家代码
     */
    private String countryCode;

    /**
     * 地区代码
     */
    private String regionCode;

    /**
     * 当前时间
     */
    private LocalDateTime now;

    /**
     * 当前星期 (1-7, 1=周一)
     */
    private Integer dayOfWeek;

    /**
     * 当前小时 (0-23)
     */
    private Integer hourOfDay;

    /**
     * 页面域名
     */
    private String domain;

    /**
     * 页面URL
     */
    private String pageUrl;

    /**
     * 请求底价
     */
    private BigDecimal requestFloorPrice;

    /**
     * 是否测试请求
     */
    private boolean test;

    /**
     * 扩展属性
     */
    private Map<String, Object> attributes;

    /**
     * 构造函数
     */
    public BidContext(BidRequest request, Imp imp) {
        this.request = request;
        this.currentImp = imp;
        this.device = request.getDevice();
        this.user = request.getUser();
        this.now = LocalDateTime.now();
        this.dayOfWeek = now.getDayOfWeek().getValue();
        this.hourOfDay = now.getHour();
        this.attributes = new HashMap<>();

        // 提取设备信息
        if (device != null) {
            this.deviceType = device.getDeviceType();
            this.os = device.getOs();
            this.geo = device.getGeo();
            if (geo != null) {
                this.countryCode = geo.getCountry();
                this.regionCode = geo.getRegion();
            }
        }

        // 提取用户ID
        if (user != null && user.getId() != null) {
            this.userId = user.getId();
        }

        // 提取页面信息
        if (request.getSite() != null) {
            this.domain = request.getSite().getDomain();
            this.pageUrl = request.getSite().getPage();
        } else if (request.getApp() != null) {
            this.domain = request.getApp().getBundle();
        }

        // 提取底价
        if (imp != null && imp.getBidFloor() != null) {
            this.requestFloorPrice = imp.getBidFloor();
        }

        // 测试标识
        this.test = request.getTest() != null && request.getTest() == 1;
    }

    /**
     * 获取扩展属性
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }

    /**
     * 设置扩展属性
     */
    public void setAttribute(String key, Object value) {
        this.attributes.put(key, value);
    }

    /**
     * 获取完整的广告组定位key (用于缓存)
     */
    public String getAdGroupLocationKey() {
        return String.format("%s:%s:%s:%s",
                countryCode != null ? countryCode : "*",
                regionCode != null ? regionCode : "*",
                deviceType != null ? deviceType : "*",
                os != null ? os : "*");
    }

    // Getters and Setters

    public BidRequest getRequest() {
        return request;
    }

    public Imp getCurrentImp() {
        return currentImp;
    }

    public Device getDevice() {
        return device;
    }

    public Geo getGeo() {
        return geo;
    }

    public User getUser() {
        return user;
    }

    public String getUserId() {
        return userId;
    }

    public Integer getDeviceType() {
        return deviceType;
    }

    public String getOs() {
        return os;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public LocalDateTime getNow() {
        return now;
    }

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public Integer getHourOfDay() {
        return hourOfDay;
    }

    public String getDomain() {
        return domain;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public BigDecimal getRequestFloorPrice() {
        return requestFloorPrice;
    }

    public boolean isTest() {
        return test;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
