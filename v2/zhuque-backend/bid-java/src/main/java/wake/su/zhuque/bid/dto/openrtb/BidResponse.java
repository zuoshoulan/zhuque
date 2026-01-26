package wake.su.zhuque.bid.dto.openrtb;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * OpenRTB 2.6 Bid Response
 * <p>
 * 竞价响应的顶层对象
 * </p>
 */
@Schema(
    description = """
        OpenRTB 2.6 竞价响应对象（BidResponse）

        DSP返回给SSP的竞价响应，包含出价信息。

        **必需字段**：
        - id: 必须与请求中的id匹配
        - seatbid: 至少包含一个SeatBid对象

        **响应状态**：
        - 有竞价：返回200，包含seatbid
        - 无竞价：返回204 No Content，或在响应中设置nbr字段
        """
)
public class BidResponse {

    @Schema(
        description = """
            响应ID

            必须与请求中的id完全匹配，用于关联请求和响应。
            """,
        example = "80ce30c53c16e6ede735fe1227162d61",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("id")
    private String id;

    @Schema(
        description = """
            座位竞价列表（SeatBid）

            包含至少一个SeatBid对象，每个对象代表一个买家的竞价。
            """,
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("seatbid")
    private List<SeatBid> seatbid;

    @Schema(
        description = """
            竞价响应ID（Bid ID）

            DSP生成的唯一响应标识符，用于日志追踪和调试。
            """,
        example = "bid-response-12345"
    )
    @JsonProperty("bidid")
    private String bidId;

    @Schema(
        description = """
            货币代码（Currency）

            所有竞价价格使用的货币，使用ISO-4217标准。
            如果不指定，默认为USD。
            """,
        example = "CNY"
    )
    @JsonProperty("cur")
    private String currency;

    @Schema(
        description = """
            自定义数据

            DSP传递给SSP的自定义数据，原样返回。
            """)
    @JsonProperty("customdata")
    private String customData;

    @Schema(
        description = """
            无竞价原因代码（No Bid Reason）

            当DSP不竞价时，指定原因代码：
            - 0: 未知错误
            - 1: 技术错误
            - 2: 无效请求
            - 3: 已知网络蜘蛛
            - 4: 疑似不诚实流量
            - 5: 每小时/每天预算不足
            - 6: 每天预算不足
            - 7: 无匹配内容
            - 8: 开始时间或结束时间不匹配
            - 9: 无匹配广告
            - 10: 地域过滤
            """,
        example = "7"
    )
    @JsonProperty("nbr")
    private Integer noBidReason;

    @Schema(
        description = """
            扩展字段

            用于自定义数据的扩展对象。
            """)
    @JsonProperty("ext")
    private Object ext;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<SeatBid> getSeatbid() {
        return seatbid;
    }

    public void setSeatbid(List<SeatBid> seatbid) {
        this.seatbid = seatbid;
    }

    public String getBidId() {
        return bidId;
    }

    public void setBidId(String bidId) {
        this.bidId = bidId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCustomData() {
        return customData;
    }

    public void setCustomData(String customData) {
        this.customData = customData;
    }

    public Integer getNoBidReason() {
        return noBidReason;
    }

    public void setNoBidReason(Integer noBidReason) {
        this.noBidReason = noBidReason;
    }

    public Object getExt() {
        return ext;
    }

    public void setExt(Object ext) {
        this.ext = ext;
    }
}
