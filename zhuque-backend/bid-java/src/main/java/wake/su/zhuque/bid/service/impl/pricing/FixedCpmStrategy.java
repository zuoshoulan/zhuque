package wake.su.zhuque.bid.service.impl.pricing;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import wake.su.zhuque.bid.service.pricing.BidPriceStrategy;

/**
 * 固定CPM出价策略
 *
 * @author zhuque
 * @version 1.0
 */
@Component
public class FixedCpmStrategy implements BidPriceStrategy {

  @Override
  public Long calculate(BigDecimal basePrice, BigDecimal maxPrice, BigDecimal minPrice, BigDecimal floorPrice,
      Double predictedCtr) {
    // 使用基础出价
    BigDecimal bidPrice = basePrice;

    // 约束在 [max(floor, min), max] 范围内
    if (minPrice != null) {
      bidPrice = bidPrice.max(minPrice);
    }
    bidPrice = bidPrice.max(floorPrice);

    if (maxPrice != null) {
      bidPrice = bidPrice.min(maxPrice);
    }

    // 转换为微元/千次
    return bidPrice.multiply(BigDecimal.valueOf(1000)).longValue();
  }

  @Override
  public Integer getType() {
    return 1;
  }
}
