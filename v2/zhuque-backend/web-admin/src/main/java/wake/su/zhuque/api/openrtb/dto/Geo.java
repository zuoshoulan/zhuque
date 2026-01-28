package wake.su.zhuque.api.openrtb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * OpenRTB 地理位置对象
 */
@Data
@Schema(description = "地理位置信息对象")
public class Geo {

  @JsonProperty("lat")
  @Schema(description = "纬度(-90到90)")
  private Float lat;

  @JsonProperty("lon")
  @Schema(description = "经度(-180到180)")
  private Float lon;

  @JsonProperty("type")
  @Schema(description = "位置源方法")
  private Integer type;

  @JsonProperty("accuracy")
  @Schema(description = "位置精度(米)")
  private Integer accuracy;

  @JsonProperty("lastfix")
  @Schema(description = "位置确定时间(Unix时间戳)")
  private Long lastfix;

  @JsonProperty("ipservice")
  @Schema(description = "IP位置服务提供商")
  private Integer ipservice;

  @JsonProperty("country")
  @Schema(description = "国家代码(ISO-3166-1-alpha-3)")
  private String country;

  @JsonProperty("region")
  @Schema(description = "区域代码(例如ISO-3166-2)")
  private String region;

  @JsonProperty("regionfips104")
  @Schema(description = "地区FIPS 104代码")
  private String regionfips104;

  @JsonProperty("metro")
  @Schema(description = "大都市区域代码")
  private String metro;

  @JsonProperty("city")
  @Schema(description = "城市名称")
  private String city;

  @JsonProperty("zip")
  @Schema(description = "邮政编码")
  private String zip;

  @JsonProperty("zipext")
  @Schema(description = "邮政编码扩展")
  private String zipext;

  @JsonProperty("utcOffset")
  @Schema(description = "UTC偏移量(分钟)")
  private Integer utcOffset;

  @JsonProperty("ext")
  @Schema(description = "扩展字段")
  private Object ext;
}
