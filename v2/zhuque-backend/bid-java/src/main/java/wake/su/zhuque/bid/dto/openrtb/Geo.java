package wake.su.zhuque.bid.dto.openrtb;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * OpenRTB 2.6 Geo Object
 * <p>
 * 地理位置信息对象
 * </p>
 */
@Schema(
    description = """
        OpenRTB 2.6 地理位置对象（Geo）

        包含用户的地理位置信息，用于地域定向。

        **位置来源类型**：
        - 1: GPS/精确位置
        - 2: IP位置
        - 3: 用户提供

        **常用字段**：
        - country: 国家代码（ISO-3166-1-alpha-3）
        - region: 区域代码（ISO-3166-2）
        - city: 城市名称
        - lat/lon: 经纬度坐标
        """
)
public class Geo {

    @Schema(
        description = """
            纬度（Latitude）

            地理位置的纬度坐标，范围-90到90。
            """,
        example = "39.9042"
    )
    @JsonProperty("lat")
    private BigDecimal lat;

    @Schema(
        description = """
            经度（Longitude）

            地理位置的经度坐标，范围-180到180。
            """,
        example = "116.4074"
    )
    @JsonProperty("lon")
    private BigDecimal lon;

    @Schema(
        description = """
            位置来源类型

            - 1: GPS/精确位置（最精确）
            - 2: IP位置（基于IP地址推断）
            - 3: 用户提供（用户注册时设置）
            """,
        example = "2"
    )
    @JsonProperty("type")
    private Integer type;

    @Schema(
        description = """
            精度（Accuracy）

            位置的精确度，单位为米。
            值越小表示位置越精确。
            """,
        example = "100"
    )
    @JsonProperty("accuracy")
    private Integer accuracy;

    @Schema(
        description = """
            最后定位时间（Last Fix）

            最后一次定位的时间戳（Unix时间戳，秒）。
            """,
        example = "1706256000"
    )
    @JsonProperty("lastfix")
    private Long lastFix;

    @Schema(
        description = """
            IP位置服务

            提供IP位置定位的服务商ID。
            """)
    @JsonProperty("ipservice")
    private Integer ipService;

    @Schema(
        description = """
            国家代码（Country）

            使用ISO-3166-1-alpha-3标准（3字母代码）。
            如：CHN, USA, JPN
            """,
        example = "CHN"
    )
    @JsonProperty("country")
    private String country;

    @Schema(
        description = """
            区域代码（Region）

            使用ISO-3166-2标准。
            中国示例：BJ（北京）、SH（上海）、GD（广东）
            """,
        example = "BJ"
    )
    @JsonProperty("region")
    private String region;

    @Schema(
        description = """
            FIPS区域代码

            美国联邦信息处理标准区域代码。
            主要用于美国地区。
            """)
    @JsonProperty("regionfips")
    private String regionFips;

    @Schema(
        description = """
            大都市区域代码（Metro）

            主要用于美国，如：NY（纽约）、LA（洛杉矶）。
            """)
    @JsonProperty("metro")
    private String metro;

    @Schema(
        description = """
            城市名称（City）

            城市的名称，通常使用英文名或本地语言名。
            """,
        example = "Beijing"
    )
    @JsonProperty("city")
    private String city;

    @Schema(
        description = """
            邮政编码（Zip Code）

            邮政编码或邮编。
            """,
        example = "100000"
    )
    @JsonProperty("zip")
    private String zip;

    @Schema(
        description = """
            UTC偏移（UTC Offset）

            当地时区与UTC的偏移量，单位为分钟。
            如：480（UTC+8，北京时间）
            """,
        example = "480"
    )
    @JsonProperty("utcoffset")
    private Integer utcOffset;

    @Schema(
        description = """
            扩展字段

            用于自定义数据的扩展对象。
            """)
    @JsonProperty("ext")
    private Object ext;

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public BigDecimal getLon() {
        return lon;
    }

    public void setLon(BigDecimal lon) {
        this.lon = lon;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Integer accuracy) {
        this.accuracy = accuracy;
    }

    public Long getLastFix() {
        return lastFix;
    }

    public void setLastFix(Long lastFix) {
        this.lastFix = lastFix;
    }

    public Integer getIpService() {
        return ipService;
    }

    public void setIpService(Integer ipService) {
        this.ipService = ipService;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegionFips() {
        return regionFips;
    }

    public void setRegionFips(String regionFips) {
        this.regionFips = regionFips;
    }

    public String getMetro() {
        return metro;
    }

    public void setMetro(String metro) {
        this.metro = metro;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public Integer getUtcOffset() {
        return utcOffset;
    }

    public void setUtcOffset(Integer utcOffset) {
        this.utcOffset = utcOffset;
    }

    public Object getExt() {
        return ext;
    }

    public void setExt(Object ext) {
        this.ext = ext;
    }
}
