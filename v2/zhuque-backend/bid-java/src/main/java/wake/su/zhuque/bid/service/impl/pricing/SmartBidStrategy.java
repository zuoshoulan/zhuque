package wake.su.zhuque.bid.service.impl.pricing;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import wake.su.zhuque.bid.service.pricing.BidPriceStrategy;

/**
 * 智能出价策略 根据预测CTR动态调整出价
 *
 * @author zhuque
 * @version 1.0
 */
@Component
public class SmartBidStrategy implements BidPriceStrategy {

  @Override
  public Long calculate(BigDecimal basePrice, BigDecimal maxPrice, BigDecimal minPrice, BigDecimal floorPrice,
      Double predictedCtr) {
    // 根据pCTR调整出价
    double ctrFactor = predictedCtr != null ? predictedCtr : 1.0;

    // 限制调整范围在 [0.5, 2.0] 之间
    ctrFactor = Math.max(0.5, Math.min(2.0, ctrFactor));

    BigDecimal bidPrice = basePrice.multiply(BigDecimal.valueOf(ctrFactor));

    // 约束在 [max(floor, min), max] 范围内
    if(minPrice != null) {
      bidPrice = bidPrice.max(minPrice);
    }
    bidPrice = bidPrice.max(floorPrice);

    if(maxPrice != null) {
      bidPrice = bidPrice.min(maxPrice);
    }

    // 转换为微元/千次
    return bidPrice.multiply(BigDecimal.valueOf(1000)).longValue();
  }

  @Override
  public Integer getType() {
    return 2;
  }
}
