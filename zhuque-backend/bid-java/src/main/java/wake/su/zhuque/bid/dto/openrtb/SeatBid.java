package wake.su.zhuque.bid.dto.openrtb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * OpenRTB 2.6 SeatBid Object
 *
 * <p>座位竞价对象，包含一个买家的竞价响应
 */
@Schema(description = """
    OpenRTB 2.6 座位竞价对象（SeatBid）

    代表一个买家（DSP）的竞价响应，可以包含对多个展示机会的出价。

    **常用场景**：
    - 单个DSP对多个广告位同时出价
    - 多个DSP的竞价会包含多个SeatBid对象
    """)
public class SeatBid {

  @Schema(description = """
      竞价列表（Bid）

      包含该买家对所有展示机会的出价。
      每个Bid对象对应一个展示机会（通过impid关联）。
      """, requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("bid")
  private List<Bid> bid;

  @Schema(description = """
      买家ID/座位ID（Seat）

      买家的标识符，用于区分不同的DSP。
      如果不指定，默认为第一个seat或DSP的主ID。
      """, example = "dsp-001")
  @JsonProperty("seat")
  private String seat;

  @Schema(description = """
      分组标志（Group）

      指示竞价是否作为一组处理。
      - 0: 竞价独立，可以部分胜出
      - 1: 竞价是一组，要么全部胜出，要么全部不竞价

      主要用于多广告位联投的场景。
      """, example = "0")
  @JsonProperty("group")
  private Integer group;

  @Schema(description = """
      扩展字段

      用于自定义数据的扩展对象。
      """)
  @JsonProperty("ext")
  private Object ext;

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

  public Object getExt() {
    return ext;
  }

  public void setExt(Object ext) {
    this.ext = ext;
  }
}
