package wake.su.zhuque.api.openrtb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * OpenRTB 席位竞价对象
 */
@Data
@Schema(description = "席位竞价对象")
public class SeatBid {

    @JsonProperty("bid")
    @Schema(description = "竞价对象数组", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Bid> bid;

    @JsonProperty("seat")
    @Schema(description = "席位ID")
    private String seat;

    @JsonProperty("group")
    @Schema(description = "是否作为一组竞价：0=否，1=是")
    private Integer group;

    @JsonProperty("ext")
    @Schema(description = "扩展字段")
    private Object ext;
}
