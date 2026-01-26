package wake.su.zhuque.bid.service.pricing;

import java.math.BigDecimal;

/**
 * 出价策略接口
 *
 * @author zhuque
 * @version 1.0
 */
public interface BidPriceStrategy {

  /**
   * 计算出价
   *
   * @param basePrice 基础出价 (元)
   * @param maxPrice 最高出价 (元)
   * @param minPrice 最低出价 (元)
   * @param floorPrice 底价 (元)
   * @param predictedCtr 预测CTR (0~1)
   * @return 出价（微元/千次单位）
   */
  Long calculate(
      BigDecimal basePrice,
      BigDecimal maxPrice,
      BigDecimal minPrice,
      BigDecimal floorPrice,
      Double predictedCtr);

  /**
   * 获取策略类型
   *
   * @return 策略类型 (1=固定CPM, 2=智能出价, 3=目标CPA, 4=最高赢价)
   */
  Integer getType();
}
