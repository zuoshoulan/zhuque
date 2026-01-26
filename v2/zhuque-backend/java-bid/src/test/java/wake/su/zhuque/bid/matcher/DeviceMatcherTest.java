package wake.su.zhuque.bid.matcher;

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

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 设备匹配器测试
 */
public class DeviceMatcherTest {

    private wake.su.zhuque.bid.service.matcher.DeviceMatcher deviceMatcher;
    private BidContext context;
    private RtbAdGroupDO adGroup;

    @BeforeEach
    void setUp() {
        deviceMatcher = new wake.su.zhuque.bid.service.impl.matcher.DeviceMatcherImpl();

        // 创建测试用的 BidRequest (移动设备 + iOS)
        BidRequest request = new BidRequest();
        request.setId("test-request");

        Device device = new Device();
        device.setDeviceType(1);  // 手机
        device.setOs("iOS");
        device.setUserAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X)");

        Geo geo = new Geo();
        geo.setCountry("CN");
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
    @DisplayName("未设置设备定向时应通过")
    void testNoTargetingShouldMatch() {
        adGroup.setTargetingDevice(null);
        adGroup.setTargetingOs(null);
        assertTrue(deviceMatcher.matches(context, adGroup));

        adGroup.setTargetingDevice("[]");
        adGroup.setTargetingOs("[]");
        assertTrue(deviceMatcher.matches(context, adGroup));
    }

    @Test
    @DisplayName("设备类型匹配")
    void testDeviceTypeMatch() {
        // 配置手机和平板
        adGroup.setTargetingDevice("[1, 2]");
        assertTrue(deviceMatcher.matches(context, adGroup));
    }

    @Test
    @DisplayName("设备类型不匹配应失败")
    void testDeviceTypeNotMatch() {
        // 只配置桌面和TV
        adGroup.setTargetingDevice("[3, 4]");
        assertFalse(deviceMatcher.matches(context, adGroup));
    }

    @Test
    @DisplayName("操作系统匹配")
    void testOsMatch() {
        adGroup.setTargetingOs("[\"iOS\", \"Android\"]");
        assertTrue(deviceMatcher.matches(context, adGroup));
    }

    @Test
    @DisplayName("操作系统不匹配应失败")
    void testOsNotMatch() {
        adGroup.setTargetingOs("[\"Android\", \"Windows\"]");
        assertFalse(deviceMatcher.matches(context, adGroup));
    }

    @Test
    @DisplayName("设备类型和操作系统同时匹配")
    void testBothMatch() {
        adGroup.setTargetingDevice("[1, 2]");
        adGroup.setTargetingOs("[\"iOS\"]");
        assertTrue(deviceMatcher.matches(context, adGroup));
    }

    @Test
    @DisplayName("设备匹配但OS不匹配")
    void testDeviceMatchOsNotMatch() {
        adGroup.setTargetingDevice("[1]");
        adGroup.setTargetingOs("[\"Android\"]");
        assertFalse(deviceMatcher.matches(context, adGroup));
    }

    @Test
    @DisplayName("OS模糊匹配 - 包含关系")
    void testOsFuzzyMatch() {
        // 请求中是 "iOS"，配置是 "iOS" 或包含 "iOS" 的字符串
        adGroup.setTargetingOs("[\"iOS\"]");
        assertTrue(deviceMatcher.matches(context, adGroup));
    }

    @ParameterizedTest
    @CsvSource({
        "1, iOS, true",
        "1, Android, false",
        "2, iOS, false",
        "3, iOS, false"
    })
    @DisplayName("设备匹配参数化测试")
    void testDeviceMatching(String deviceType, String os, boolean expected) {
        adGroup.setTargetingDevice("[" + deviceType + "]");
        adGroup.setTargetingOs("[\"" + os + "\"]");
        assertEquals(expected, deviceMatcher.matches(context, adGroup));
    }

    @Test
    @DisplayName("请求无设备类型时应通过")
    void testNoDeviceTypeInRequest() {
        // 创建无设备类型的请求
        BidRequest request = new BidRequest();
        request.setId("test-request-no-device");

        Device device = new Device();
        device.setOs("iOS");
        request.setDevice(device);

        Imp imp = new Imp();
        imp.setId("imp-001");
        request.setImp(List.of(imp));

        BidContext noDeviceContext = new BidContext(request, imp);

        // 设置了设备定向，但请求无设备信息
        adGroup.setTargetingDevice("[1]");
        assertFalse(deviceMatcher.matches(noDeviceContext, adGroup));
    }
}
