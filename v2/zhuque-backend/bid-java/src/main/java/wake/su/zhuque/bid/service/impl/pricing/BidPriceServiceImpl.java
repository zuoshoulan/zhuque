package wake.su.zhuque.bid.service.impl.pricing;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import wake.su.zhuque.bid.context.BidContext;
import wake.su.zhuque.bid.service.pricing.BidPriceService;
import wake.su.zhuque.bid.service.pricing.BidPriceStrategy;
import wake.su.zhuque.model.entity.RtbAdGroupDO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 出价计算服务实现
 *
 * @author zhuque
 * @version 1.0
 */

@Service
@RequiredArgsConstructor
public class BidPriceServiceImpl implements BidPriceService {

    private static final Logger log = LoggerFactory.getLogger(BidPriceServiceImpl.class);

    private final List<BidPriceStrategy> strategies;
    private Map<Integer, BidPriceStrategy> strategyMap;

    @Override
    public Long calculateBidPrice(RtbAdGroupDO adGroup, BidContext context) {
        // 懒加载策略映射
        if (strategyMap == null) {
            strategyMap = strategies.stream()
                    .collect(Collectors.toMap(BidPriceStrategy::getType, Function.identity()));
        }

        // 获取出价策略
        Integer bidStrategy = adGroup.getBidStrategy();
        if (bidStrategy == null) {
            bidStrategy = 1;  // 默认固定CPM
        }

        BidPriceStrategy strategy = strategyMap.get(bidStrategy);
        if (strategy == null) {
            log.warn("未找到出价策略: {}, 使用固定CPM", bidStrategy);
            strategy = strategyMap.get(1);
        }

        // 计算出价
        BigDecimal basePrice = adGroup.getBaseBidPrice();
        BigDecimal maxPrice = adGroup.getMaxBid();
        BigDecimal minPrice = adGroup.getBidFloor();
        BigDecimal floorPrice = context.getRequestFloorPrice();
        if (floorPrice == null) {
            floorPrice = BigDecimal.ZERO;
        }

        Double predictedCtr = 1.0;  // TODO: 从 pCTR 服务获取

        return strategy.calculate(basePrice, maxPrice, minPrice, floorPrice, predictedCtr);
    }
}
