package wake.su.zhuque.bid.matcher;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import wake.su.zhuque.bid.context.BidContext;
import wake.su.zhuque.bid.dto.openrtb.BidRequest;
import wake.su.zhuque.bid.dto.openrtb.Device;
import wake.su.zhuque.bid.dto.openrtb.Geo;
import wake.su.zhuque.bid.dto.openrtb.Imp;
import wake.su.zhuque.model.entity.RtbAdGroupDO;

/** 地域匹配器测试 */
public class GeoMatcherTest {

  private wake.su.zhuque.bid.service.matcher.GeoMatcher geoMatcher;
  private BidContext context;
  private RtbAdGroupDO adGroup;

  @BeforeEach
  void setUp() {
    geoMatcher = new wake.su.zhuque.bid.service.impl.matcher.GeoMatcherImpl();

    // 创建测试用的 BidRequest
    BidRequest request = new BidRequest();
    request.setId("test-request");

    Device device = new Device();
    Geo geo = new Geo();
    geo.setCountry("CN");
    geo.setRegion("Beijing");
    device.setGeo(geo);
    request.setDevice(device);

    Imp imp = new Imp();
    imp.setId("imp-001");
    request.setImp(List.of(imp));

    context = new BidContext(request, imp);

    // 创建测试用的广告组
    adGroup = new RtbAdGroupDO();
    adGroup.setId(1L);
    adGroup.setName("测试广告组");
    adGroup.setCampaignId(100L);
    adGroup.setAdvertiserId(1000L);
    adGroup.setStatus(1);
    adGroup.setBaseBidPrice(BigDecimal.valueOf(1.0));
    adGroup.setMaxBid(BigDecimal.valueOf(5.0));
  }

  @Test
  @DisplayName("未设置地域定向时应通过")
  void testNoTargetingShouldMatch() {
    adGroup.setTargetingGeo(null);
    assertTrue(geoMatcher.matches(context, adGroup));

    adGroup.setTargetingGeo("");
    assertTrue(geoMatcher.matches(context, adGroup));

    adGroup.setTargetingGeo("[]");
    assertTrue(geoMatcher.matches(context, adGroup));
  }

  @Test
  @DisplayName("国家代码匹配")
  void testCountryMatch() {
    // 只配置国家
    adGroup.setTargetingGeo("[\"CN\", \"US\"]");
    assertTrue(geoMatcher.matches(context, adGroup));
  }

  @Test
  @DisplayName("国家代码不匹配应失败")
  void testCountryNotMatch() {
    adGroup.setTargetingGeo("[\"US\", \"JP\"]");
    assertFalse(geoMatcher.matches(context, adGroup));
  }

  @Test
  @DisplayName("省份匹配")
  void testRegionMatch() {
    // 配置国家+省份
    adGroup.setTargetingGeo("[\"CN\", \"CN-Beijing\", \"CN-Shanghai\"]");
    assertTrue(geoMatcher.matches(context, adGroup));
  }

  @Test
  @DisplayName("省份不匹配应失败")
  void testRegionNotMatch() {
    // 只配置其他省份
    adGroup.setTargetingGeo("[\"CN-Shanghai\", \"CN-Guangdong\"]");
    assertFalse(geoMatcher.matches(context, adGroup));
  }

  @ParameterizedTest
  @CsvSource({"CN-Beijing, true", "CN-Shanghai, false", "US, false"})
  @DisplayName("地域匹配参数化测试")
  void testGeoMatching(String targetingGeo, boolean expected) {
    adGroup.setTargetingGeo("[\"" + targetingGeo + "\"]");
    assertEquals(expected, geoMatcher.matches(context, adGroup));
  }

  @Test
  @DisplayName("请求无地域信息时应失败")
  void testNoGeoInRequest() {
    // 创建无地域信息的请求
    BidRequest request = new BidRequest();
    request.setId("test-request-no-geo");

    Device device = new Device();
    device.setGeo(null);
    request.setDevice(device);

    Imp imp = new Imp();
    imp.setId("imp-001");
    request.setImp(List.of(imp));

    BidContext noGeoContext = new BidContext(request, imp);

    // 设置了地域定向，但请求无地域信息
    adGroup.setTargetingGeo("[\"CN\"]");
    assertFalse(geoMatcher.matches(noGeoContext, adGroup));
  }
}
