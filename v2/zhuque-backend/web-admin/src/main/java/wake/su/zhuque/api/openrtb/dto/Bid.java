package wake.su.zhuque.api.openrtb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * OpenRTB 竞价对象
 */
@Data
@Schema(description = "单个竞价对象")
public class Bid {

    @JsonProperty("id")
    @Schema(description = "竞价的唯一标识符")
    private String id;

    @JsonProperty("impid")
    @Schema(description = "所竞价展示的ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String impid;

    @JsonProperty("price")
    @Schema(description = "竞价价格(微美元，例如$1.23=1230000)", requiredMode = Schema.RequiredMode.REQUIRED)
    private Float price;

    @JsonProperty("adid")
    @Schema(description = "广告ID")
    private String adid;

    @JsonProperty("nurl")
    @Schema(description = "获胜通知URL")
    private String nurl;

    @JsonProperty("burl")
    @Schema(description = "计费通知URL")
    private String burl;

    @JsonProperty("lurl")
    @Schema(description = "失败通知URL")
    private String lurl;

    @JsonProperty("adomain")
    @Schema(description = "广告主域")
    private List<String> adomain;

    @JsonProperty("bundle")
    @Schema(description = "应用包或捆绑标识符")
    private String bundle;

    @JsonProperty("iurl")
    @Schema(description = "创意图像URL")
    private String iurl;

    @JsonProperty("cid")
    @Schema(description = "创意ID")
    private String cid;

    @JsonProperty("crid")
    @Schema(description = "创意版本ID")
    private String crid;

    @JsonProperty("cat")
    @Schema(description = "IAB内容类别")
    private List<String> cat;

    @JsonProperty("attr")
    @Schema(description = "创意属性")
    private List<Integer> attr;

    @JsonProperty("api")
    @Schema(description = "API框架")
    private List<Integer> api;

    @JsonProperty("protocol")
    @Schema(description = "视频响应协议")
    private Integer protocol;

    @JsonProperty("qagmediarating")
    @Schema(description = "IQG媒体评级")
    private Integer qagmediarating;

    @JsonProperty("language")
    @Schema(description = "创意语言(ISO-639-1-alpha-2)")
    private String language;

    @JsonProperty("dealid")
    @Schema(description = "交易ID")
    private String dealid;

    @JsonProperty("w")
    @Schema(description = "创意宽度(像素)")
    private Integer w;

    @JsonProperty("h")
    @Schema(description = "创意高度(像素)")
    private Integer h;

    @JsonProperty("ratio")
    @Schema(description = "宽高比(例如100表示1:1)")
    private Integer ratio;

    @JsonProperty("dur")
    @Schema(description = "视频或音频创意持续时间(秒)")
    private Integer dur;

    @JsonProperty("mime")
    @Schema(description = "创意MIME类型")
    private String mime;

    @JsonProperty("adm")
    @Schema(description = "广告标记")
    private String adm;

    @JsonProperty("ext")
    @Schema(description = "扩展字段")
    private Object ext;
}
