package wake.su.zhuque.bid;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * 竞价服务测试套件
 *
 * <p>运行所有单元测试: mvn test -pl java-bid
 *
 * <p>运行特定测试类: mvn test -pl java-bid -Dtest=GeoMatcherTest
 *
 * <p>运行特定测试方法: mvn test -pl java-bid -Dtest=GeoMatcherTest#testCountryMatch
 */
@Suite
@SelectClasses({
  // Matcher 测试
  wake.su.zhuque.bid.matcher.GeoMatcherTest.class,
  wake.su.zhuque.bid.matcher.DeviceMatcherTest.class,
  wake.su.zhuque.bid.matcher.ScheduleMatcherTest.class,

  // Filter 测试
  wake.su.zhuque.bid.filter.BudgetFilterTest.class,
  wake.su.zhuque.bid.filter.FrequencyFilterTest.class,

  // Strategy 测试
  wake.su.zhuque.bid.pricing.BidPriceStrategyTest.class,

  // Controller 测试
  wake.su.zhuque.bid.controller.OpenRtbControllerTest.class,

  // 集成测试
  wake.su.zhuque.bid.service.RtbBidServiceIntegrationTest.class,
})
public class BidServiceTestSuite {}
