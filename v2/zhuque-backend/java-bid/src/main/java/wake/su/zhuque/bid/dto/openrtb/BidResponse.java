package wake.su.zhuque.bid.dto.openrtb;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * OpenRTB 2.6 Bid Response
 */
public class BidResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("seatbid")
    private List<SeatBid> seatbid;

    @JsonProperty("bidid")
    private String bidId;

    @JsonProperty("cur")
    private String currency;

    @JsonProperty("customdata")
    private String customData;

    @JsonProperty("nbr")
    private Integer noBidReason;

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
