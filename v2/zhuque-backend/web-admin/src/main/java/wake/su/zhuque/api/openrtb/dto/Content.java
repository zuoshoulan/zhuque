package wake.su.zhuque.api.openrtb.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * OpenRTB 内容对象
 */
@Data
@Schema(description = "内容信息对象")
public class Content {

  @JsonProperty("id")
  @Schema(description = "内容唯一标识符")
  private String id;

  @JsonProperty("episode")
  @Schema(description = "剧集号")
  private Integer episode;

  @JsonProperty("title")
  @Schema(description = "内容标题")
  private String title;

  @JsonProperty("series")
  @Schema(description = "系列名称")
  private String series;

  @JsonProperty("season")
  @Schema(description = "季")
  private String season;

  @JsonProperty("artist")
  @Schema(description = "艺术家")
  private String artist;

  @JsonProperty("genre")
  @Schema(description = "流派")
  private String genre;

  @JsonProperty("album")
  @Schema(description = "专辑")
  private String album;

  @JsonProperty("isrc")
  @Schema(description = "国际标准录制代码(ISRC)")
  private String isrc;

  @JsonProperty("producer")
  @Schema(description = "制作者信息")
  private Producer producer;

  @JsonProperty("url")
  @Schema(description = "内容具体URL")
  private String url;

  @JsonProperty("cat")
  @Schema(description = "IAB内容类别")
  private List<String> cat;

  @JsonProperty("prodq")
  @Schema(description = "生产质量")
  private Integer prodq;

  @JsonProperty("videoq")
  @Schema(description = "视频质量")
  private Integer videoq;

  @JsonProperty("context")
  @Schema(description = "内容上下文")
  private Integer context;

  @JsonProperty("contentrating")
  @Schema(description = "内容评级")
  private String contentrating;

  @JsonProperty("userrating")
  @Schema(description = "用户评级")
  private String userrating;

  @JsonProperty("qagmediarating")
  @Schema(description = "IQG媒体评级")
  private Integer qagmediarating;

  @JsonProperty("keywords")
  @Schema(description = "关键词")
  private String keywords;

  @JsonProperty("livestream")
  @Schema(description = "是否实时流：0=否，1=是")
  private Integer livestream;

  @JsonProperty("sourceRelationship")
  @Schema(description = "内容源关系")
  private Integer sourceRelationship;

  @JsonProperty("len")
  @Schema(description = "内容长度(秒)")
  private Integer len;

  @JsonProperty("language")
  @Schema(description = "内容语言(ISO-639-1-alpha-2)")
  private String language;

  @JsonProperty("embeddable")
  @Schema(description = "是否可嵌入：0=否，1=是")
  private Integer embeddable;

  @JsonProperty("data")
  @Schema(description = "数据对象数组")
  private List<DataDto> data;

  @JsonProperty("ext")
  @Schema(description = "扩展字段")
  private Object ext;
}
