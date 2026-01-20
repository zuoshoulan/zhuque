package wake.su.zhuque.api.openrtb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * OpenRTB 交易对象
 */
@Data
@Schema(description = "直接交易对象")
public class Deal {

    @JsonProperty("id")
    @Schema(description = "交易唯一标识符", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @JsonProperty("bidfloor")
    @Schema(description = "最低竞价")
    private Float bidfloor;

    @JsonProperty("bidfloorcur")
    @Schema(description = "最低竞价货币(ISO-4217)")
    private String bidfloorcur;

    @JsonProperty("at")
    @Schema(description = "拍卖类型：1=第一价格，2=第二价格加")
    private Integer at;

    @JsonProperty("wseat")
    @Schema(description = "允许竞价的买家席位白名单")
    private List<String> wseat;

    @JsonProperty("wadv")
    @Schema(description = "允许的广告主域白名单")
    private List<String> wadv;

    @JsonProperty("ext")
    @Schema(description = "扩展字段")
    private Object ext;
}
