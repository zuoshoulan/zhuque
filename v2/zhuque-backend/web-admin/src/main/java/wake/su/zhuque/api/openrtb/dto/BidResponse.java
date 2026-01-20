package wake.su.zhuque.api.openrtb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * OpenRTB 竞价响应对象
 */
@Data
@Schema(description = "竞价响应对象")
public class BidResponse {

    @JsonProperty("id")
    @Schema(description = "竞价请求的ID(必须与请求中的id匹配)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @JsonProperty("seatbid")
    @Schema(description = "SeatBid对象数组", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<SeatBid> seatbid;

    @JsonProperty("bidid")
    @Schema(description = "竞价响应的唯一标识符")
    private String bidid;

    @JsonProperty("cur")
    @Schema(description = "竞价货币(ISO-4217)", defaultValue = "USD")
    private String cur = "USD";

    @JsonProperty("customdata")
    @Schema(description = "自定义数据")
    private String customdata;

    @JsonProperty("nbr")
    @Schema(description = "不竞价原因代码")
    private Integer nbr;

    @JsonProperty("ext")
    @Schema(description = "扩展字段")
    private Object ext;
}
