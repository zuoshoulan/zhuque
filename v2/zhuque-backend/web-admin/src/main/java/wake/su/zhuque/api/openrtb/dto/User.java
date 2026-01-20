package wake.su.zhuque.api.openrtb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * OpenRTB 用户对象
 */
@Data
@Schema(description = "用户信息对象")
public class User {

    @JsonProperty("id")
    @Schema(description = "用户唯一标识符")
    private String id;

    @JsonProperty("buyeruid")
    @Schema(description = "买方分配的用户ID")
    private String buyeruid;

    @JsonProperty("yob")
    @Schema(description = "出生年份")
    private Integer yob;

    @JsonProperty("gender")
    @Schema(description = "性别：M=男，F=女，O=其他")
    private String gender;

    @JsonProperty("keywords")
    @Schema(description = "用户相关关键词")
    private String keywords;

    @JsonProperty("customdata")
    @Schema(description = "自定义数据")
    private String customdata;

    @JsonProperty("geo")
    @Schema(description = "用户地理位置")
    private Geo geo;

    @JsonProperty("data")
    @Schema(description = "数据对象数组")
    private List<Data> data;

    @JsonProperty("ext")
    @Schema(description = "扩展字段")
    private Object ext;
}
