package wake.su.zhuque.bid.context;

import java.math.BigDecimal;

import wake.su.zhuque.model.entity.RtbAdDO;
import wake.su.zhuque.model.entity.RtbAdGroupDO;

/**
 * 竞价候选对象 包含广告组、计算后的分数、出价等信息
 *
 * @author zhuque
 * @version 1.0
 */
public class BidCandidate implements Comparable<BidCandidate> {

  /** 广告组 */
  private RtbAdGroupDO adGroup;

  /** 广告 */
  private RtbAdDO ad;

  /** 竞价分数 (用于排序) */
  private Double score;

  /** 计算出的出价 (微元/千次) */
  private Long bidPrice;

  /** 请求底价 */
  private BigDecimal floorPrice;

  /** 预测CTR (0~1) */
  private Double predictedCtr;

  /** 预测CVR (0~1) */
  private Double predictedCvr;

  /** 预算检查结果 */
  private boolean budgetPassed;

  /** 频次检查结果 */
  private boolean frequencyPassed;

  /** 构造函数 */
  public BidCandidate(RtbAdGroupDO adGroup, RtbAdDO ad) {
    this.adGroup = adGroup;
    this.ad = ad;
    this.predictedCtr = 1.0; // 默认值
    this.predictedCvr = 0.0; // 默认值
  }

  /** 计算竞价分数 score = weight × bidPrice × qualityScore */
  public void calculateScore() {
    if(bidPrice == null) {
      this.score = 0.0;
      return;
    }

    double weight = adGroup.getPriority() != null ? adGroup.getPriority() : 1.0;
    double price = bidPrice / 1000.0; // 转回元
    double qualityScore = 1.0; // TODO: 后续可加入质量分

    this.score = weight * price * qualityScore;
  }

  @Override
  public int compareTo(BidCandidate other) {
    // 降序排序，分数高的在前
    int scoreCompare = Double.compare(other.score, this.score);
    if(scoreCompare != 0) {
      return scoreCompare;
    }
    // 分数相同时，按 adGroupId 排序，保证确定性
    return Long.compare(this.adGroup.getId(), other.adGroup.getId());
  }

  /** 是否所有检查都通过 */
  public boolean isAllPassed() {
    return budgetPassed && frequencyPassed;
  }

  // Getters and Setters

  public RtbAdGroupDO getAdGroup() {
    return adGroup;
  }

  public void setAdGroup(RtbAdGroupDO adGroup) {
    this.adGroup = adGroup;
  }

  public RtbAdDO getAd() {
    return ad;
  }

  public void setAd(RtbAdDO ad) {
    this.ad = ad;
  }

  public Double getScore() {
    return score;
  }

  public void setScore(Double score) {
    this.score = score;
  }

  public Long getBidPrice() {
    return bidPrice;
  }

  public void setBidPrice(Long bidPrice) {
    this.bidPrice = bidPrice;
  }

  public BigDecimal getFloorPrice() {
    return floorPrice;
  }

  public void setFloorPrice(BigDecimal floorPrice) {
    this.floorPrice = floorPrice;
  }

  public Double getPredictedCtr() {
    return predictedCtr;
  }

  public void setPredictedCtr(Double predictedCtr) {
    this.predictedCtr = predictedCtr;
  }

  public Double getPredictedCvr() {
    return predictedCvr;
  }

  public void setPredictedCvr(Double predictedCvr) {
    this.predictedCvr = predictedCvr;
  }

  public boolean isBudgetPassed() {
    return budgetPassed;
  }

  public void setBudgetPassed(boolean budgetPassed) {
    this.budgetPassed = budgetPassed;
  }

  public boolean isFrequencyPassed() {
    return frequencyPassed;
  }

  public void setFrequencyPassed(boolean frequencyPassed) {
    this.frequencyPassed = frequencyPassed;
  }
}
