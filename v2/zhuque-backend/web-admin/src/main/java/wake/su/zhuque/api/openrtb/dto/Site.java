package wake.su.zhuque.api.openrtb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * OpenRTB 网站对象
 */
@Data
@Schema(description = "网站信息对象")
public class Site {

    @JsonProperty("id")
    @Schema(description = "站点ID")
    private String id;

    @JsonProperty("name")
    @Schema(description = "站点名称")
    private String name;

    @JsonProperty("domain")
    @Schema(description = "站点域名")
    private String domain;

    @JsonProperty("cat")
    @Schema(description = "IAB内容类别")
    private List<String> cat;

    @JsonProperty("sectioncat")
    @Schema(description = "站点部分的IAB内容类别")
    private List<String> sectioncat;

    @JsonProperty("pagecat")
    @Schema(description = "页面的IAB内容类别")
    private List<String> pagecat;

    @JsonProperty("page")
    @Schema(description = "页面完整URL")
    private String page;

    @JsonProperty("ref")
    @Schema(description = "引用URL")
    private String ref;

    @JsonProperty("search")
    @Schema(description = "搜索项")
    private String search;

    @JsonProperty("mobile")
    @Schema(description = "是否针对移动优化：0=否，1=是")
    private Integer mobile;

    @JsonProperty("privacypolicy")
    @Schema(description = "是否有隐私政策：0=否，1=是")
    private Integer privacypolicy;

    @JsonProperty("publisher")
    @Schema(description = "发布商信息")
    private Publisher publisher;

    @JsonProperty("content")
    @Schema(description = "内容信息")
    private Content content;

    @JsonProperty("keywords")
    @Schema(description = "关键词列表")
    private String keywords;

    @JsonProperty("ext")
    @Schema(description = "扩展字段")
    private Object ext;
}
