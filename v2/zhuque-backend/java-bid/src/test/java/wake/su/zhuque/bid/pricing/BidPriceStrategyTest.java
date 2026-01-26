package wake.su.zhuque.bid.pricing;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wake.su.zhuque.bid.service.impl.pricing.FixedCpmStrategy;
import wake.su.zhuque.bid.service.impl.pricing.SmartBidStrategy;
import wake.su.zhuque.bid.service.pricing.BidPriceStrategy;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 出价策略测试
 */
public class BidPriceStrategyTest {

    @Test
    @DisplayName("固定CPM策略 - 基础出价")
    void testFixedCpmBasicBid() {
        BidPriceStrategy strategy = new FixedCpmStrategy();

        BigDecimal basePrice = BigDecimal.valueOf(1.0);
        BigDecimal maxPrice = BigDecimal.valueOf(5.0);
        BigDecimal minPrice = BigDecimal.valueOf(0.5);
        BigDecimal floorPrice = BigDecimal.valueOf(0.8);
        Double predictedCtr = 1.0;

        Long result = strategy.calculate(basePrice, maxPrice, minPrice, floorPrice, predictedCtr);

        // 固定CPM使用基础出价，转换为微元/千次
        assertEquals(1000L, result);  // 1.0 * 1000
    }

    @Test
    @DisplayName("固定CPM策略 - 受底价约束")
    void testFixedCpmWithFloorPrice() {
        BidPriceStrategy strategy = new FixedCpmStrategy();

        BigDecimal basePrice = BigDecimal.valueOf(0.5);
        BigDecimal maxPrice = BigDecimal.valueOf(5.0);
        BigDecimal minPrice = null;
        BigDecimal floorPrice = BigDecimal.valueOf(0.8);  // 底价高于基础出价
        Double predictedCtr = 1.0;

        Long result = strategy.calculate(basePrice, maxPrice, minPrice, floorPrice, predictedCtr);

        // 应该使用底价
        assertEquals(800L, result);  // 0.8 * 1000
    }

    @Test
    @DisplayName("固定CPM策略 - 受最高价约束")
    void testFixedCpmWithMaxPrice() {
        BidPriceStrategy strategy = new FixedCpmStrategy();

        BigDecimal basePrice = BigDecimal.valueOf(10.0);
        BigDecimal maxPrice = BigDecimal.valueOf(5.0);  // 最高价低于基础出价
        BigDecimal minPrice = null;
        BigDecimal floorPrice = BigDecimal.valueOf(0.1);
        Double predictedCtr = 1.0;

        Long result = strategy.calculate(basePrice, maxPrice, minPrice, floorPrice, predictedCtr);

        // 应该使用最高价
        assertEquals(5000L, result);  // 5.0 * 1000
    }

    @Test
    @DisplayName("固定CPM策略 - 受最低价约束")
    void testFixedCpmWithMinPrice() {
        BidPriceStrategy strategy = new FixedCpmStrategy();

        BigDecimal basePrice = BigDecimal.valueOf(0.3);
        BigDecimal maxPrice = BigDecimal.valueOf(5.0);
        BigDecimal minPrice = BigDecimal.valueOf(0.5);  // 最低价高于基础出价
        BigDecimal floorPrice = BigDecimal.valueOf(0.1);
        Double predictedCtr = 1.0;

        Long result = strategy.calculate(basePrice, maxPrice, minPrice, floorPrice, predictedCtr);

        // 应该使用最低价
        assertEquals(500L, result);  // 0.5 * 1000
    }

    @Test
    @DisplayName("固定CPM策略 - 类型应为1")
    void testFixedCpmStrategyType() {
        FixedCpmStrategy strategy = new FixedCpmStrategy();
        assertEquals(1, strategy.getType());
    }

    @Test
    @DisplayName("智能出价策略 - 基础场景")
    void testSmartBidBasic() {
        BidPriceStrategy strategy = new SmartBidStrategy();

        BigDecimal basePrice = BigDecimal.valueOf(1.0);
        BigDecimal maxPrice = BigDecimal.valueOf(5.0);
        BigDecimal minPrice = BigDecimal.valueOf(0.5);
        BigDecimal floorPrice = BigDecimal.valueOf(0.8);
        Double predictedCtr = 1.0;

        Long result = strategy.calculate(basePrice, maxPrice, minPrice, floorPrice, predictedCtr);

        // pCTR=1.0 时使用基础出价
        assertEquals(1000L, result);
    }

    @Test
    @DisplayName("智能出价策略 - 高pCTR上调出价")
    void testSmartBidHighCtr() {
        BidPriceStrategy strategy = new SmartBidStrategy();

        BigDecimal basePrice = BigDecimal.valueOf(1.0);
        BigDecimal maxPrice = BigDecimal.valueOf(5.0);
        BigDecimal minPrice = BigDecimal.valueOf(0.5);
        BigDecimal floorPrice = BigDecimal.valueOf(0.1);
        Double predictedCtr = 1.5;

        Long result = strategy.calculate(basePrice, maxPrice, minPrice, floorPrice, predictedCtr);

        // pCTR=1.5 时出价上调 (最高2倍)
        assertEquals(1500L, result);  // 1.0 * 1.5 * 1000
    }

    @Test
    @DisplayName("智能出价策略 - 低pCTR下调出价")
    void testSmartBidLowCtr() {
        BidPriceStrategy strategy = new SmartBidStrategy();

        BigDecimal basePrice = BigDecimal.valueOf(1.0);
        BigDecimal maxPrice = BigDecimal.valueOf(5.0);
        BigDecimal minPrice = BigDecimal.valueOf(0.1);
        BigDecimal floorPrice = BigDecimal.valueOf(0.1);
        Double predictedCtr = 0.7;

        Long result = strategy.calculate(basePrice, maxPrice, minPrice, floorPrice, predictedCtr);

        // pCTR=0.7 时出价下调 (最低0.5倍)
        assertEquals(700L, result);  // 1.0 * 0.7 * 1000
    }

    @Test
    @DisplayName("智能出价策略 - pCTR上限约束")
    void testSmartBidCtrUpperLimit() {
        BidPriceStrategy strategy = new SmartBidStrategy();

        BigDecimal basePrice = BigDecimal.valueOf(1.0);
        BigDecimal maxPrice = BigDecimal.valueOf(5.0);
        BigDecimal minPrice = null;
        BigDecimal floorPrice = BigDecimal.valueOf(0.1);
        Double predictedCtr = 3.0;  // 超高pCTR

        Long result = strategy.calculate(basePrice, maxPrice, minPrice, floorPrice, predictedCtr);

        // pCTR上限为2倍
        assertEquals(2000L, result);  // 1.0 * 2.0 * 1000
    }

    @Test
    @DisplayName("智能出价策略 - pCTR下限约束")
    void testSmartBidCtrLowerLimit() {
        BidPriceStrategy strategy = new SmartBidStrategy();

        BigDecimal basePrice = BigDecimal.valueOf(1.0);
        BigDecimal maxPrice = BigDecimal.valueOf(5.0);
        BigDecimal minPrice = null;
        BigDecimal floorPrice = BigDecimal.valueOf(0.1);
        Double predictedCtr = 0.2;  // 超低pCTR

        Long result = strategy.calculate(basePrice, maxPrice, minPrice, floorPrice, predictedCtr);

        // pCTR下限为0.5倍
        assertEquals(500L, result);  // 1.0 * 0.5 * 1000
    }

    @Test
    @DisplayName("智能出价策略 - null pCTR使用默认值")
    void testSmartBidNullCtr() {
        BidPriceStrategy strategy = new SmartBidStrategy();

        BigDecimal basePrice = BigDecimal.valueOf(1.0);
        BigDecimal maxPrice = BigDecimal.valueOf(5.0);
        BigDecimal minPrice = null;
        BigDecimal floorPrice = BigDecimal.valueOf(0.1);
        Double predictedCtr = null;

        Long result = strategy.calculate(basePrice, maxPrice, minPrice, floorPrice, predictedCtr);

        // null pCTR 默认为 1.0
        assertEquals(1000L, result);
    }

    @Test
    @DisplayName("智能出价策略 - 类型应为2")
    void testSmartBidStrategyType() {
        SmartBidStrategy strategy = new SmartBidStrategy();
        assertEquals(2, strategy.getType());
    }

    @ParameterizedTest
    @CsvSource({
        "1.0, 1000",   // 正常CTR
        "1.5, 1500",   // 高CTR
        "0.7, 700",    // 低CTR
        "2.5, 2000",   // 超高CTR (上限)
        "0.3, 500"     // 超低CTR (下限)
    })
    @DisplayName("智能出价策略参数化测试")
    void testSmartBidParameterized(Double ctr, Long expectedMicros) {
        BidPriceStrategy strategy = new SmartBidStrategy();

        BigDecimal basePrice = BigDecimal.valueOf(1.0);
        BigDecimal maxPrice = BigDecimal.valueOf(10.0);
        BigDecimal minPrice = null;
        BigDecimal floorPrice = BigDecimal.valueOf(0.1);

        Long result = strategy.calculate(basePrice, maxPrice, minPrice, floorPrice, ctr);
        assertEquals(expectedMicros, result);
    }
}
