package wake.su.zhuque.bid.dto.openrtb;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * OpenRTB 2.6 SeatBid Object
 */
public class SeatBid {

    @JsonProperty("bid")
    private List<Bid> bid;

    @JsonProperty("seat")
    private String seat;

    @JsonProperty("group")
    private Integer group;

    public List<Bid> getBid() {
        return bid;
    }

    public void setBid(List<Bid> bid) {
        this.bid = bid;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public Integer getGroup() {
        return group;
    }

    public void setGroup(Integer group) {
        this.group = group;
    }
}
