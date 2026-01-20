package wake.su.zhuque.api.openrtb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * OpenRTB 设备对象
 */
@Data
@Schema(description = "设备信息对象")
public class Device {

    @JsonProperty("ua")
    @Schema(description = "用户代理字符串")
    private String ua;

    @JsonProperty("geo")
    @Schema(description = "地理位置信息")
    private Geo geo;

    @JsonProperty("dnt")
    @Schema(description = "请勿跟踪标志")
    private Integer dnt;

    @JsonProperty("lmt")
    @Schema(description = "请勿跟踪标志：0=不受限，1=受限")
    private Integer lmt;

    @JsonProperty("ip")
    @Schema(description = "IP地址")
    private String ip;

    @JsonProperty("ipv6")
    @Schema(description = "IPv6地址")
    private String ipv6;

    @JsonProperty("deviceid")
    @Schema(description = "设备ID")
    private String deviceid;

    @JsonProperty("ifa")
    @Schema(description = "iOS广告标识符或Google广告ID")
    private String ifa;

    @JsonProperty("macsha1")
    @Schema(description = "MAC地址的SHA1哈希")
    private String macsha1;

    @JsonProperty("macmd5")
    @Schema(description = "MAC地址的MD5哈希")
    private String macmd5;

    @JsonProperty("carrier")
    @Schema(description = "运营商或ISP")
    private String carrier;

    @JsonProperty("language")
    @Schema(description = "浏览器语言(ISO-639-1-alpha-2)")
    private String language;

    @JsonProperty("make")
    @Schema(description = "设备制造商")
    private String make;

    @JsonProperty("model")
    @Schema(description = "设备型号")
    private String model;

    @JsonProperty("os")
    @Schema(description = "操作系统")
    private String os;

    @JsonProperty("osv")
    @Schema(description = "操作系统版本")
    private String osv;

    @JsonProperty("h")
    @Schema(description = "屏幕高度(像素)")
    private Integer h;

    @JsonProperty("w")
    @Schema(description = "屏幕宽度(像素)")
    private Integer w;

    @JsonProperty("ppi")
    @Schema(description = "每英寸像素数")
    private Integer ppi;

    @JsonProperty("pxratio")
    @Schema(description = "像素比例")
    private Float pxratio;

    @JsonProperty("js")
    @Schema(description = "是否支持JavaScript：0=否，1=是")
    private Integer js;

    @JsonProperty("connectiontype")
    @Schema(description = "连接类型")
    private Integer connectiontype;

    @JsonProperty("devicetype")
    @Schema(description = "设备类型")
    private Integer devicetype;

    @JsonProperty("ext")
    @Schema(description = "扩展字段")
    private Object ext;
}
