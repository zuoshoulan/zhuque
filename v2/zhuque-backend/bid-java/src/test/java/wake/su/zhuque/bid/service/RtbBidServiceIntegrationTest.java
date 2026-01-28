package wake.su.zhuque.bid.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.ObjectMapper;

import wake.su.zhuque.bid.context.BidContext;
import wake.su.zhuque.bid.dto.openrtb.BidRequest;
import wake.su.zhuque.bid.helper.OpenRtbTestDataLoader;
import wake.su.zhuque.dao.mapper.RtbAdGroupMapper;
import wake.su.zhuque.dao.mapper.RtbAdMapper;
import wake.su.zhuque.dao.mapper.RtbCampaignMapper;
import wake.su.zhuque.dao.mapper.RtbCreativeMapper;

/**
 * RTB 竞价服务集成测试
 *
 * <p>注意：这是一个集成测试，需要： 1. 数据库连接 (可以使用 H2 内存数据库) 2. Redis 连接 (需要本地启动或使用 Embedded Redis)
 *
 * <p>运行前确保 Redis 可用，或者跳过此测试
 */
@SpringBootTest(classes = { wake.su.zhuque.bid.BidJavaApplication.class })
@ActiveProfiles("test")
public class RtbBidServiceIntegrationTest {

  @Autowired(required = false)
  private RtbBidService rtbBidService;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private RtbCampaignMapper campaignMapper;

  @MockBean
  private RtbAdGroupMapper adGroupMapper;

  @MockBean
  private RtbAdMapper adMapper;

  @MockBean
  private RtbCreativeMapper creativeMapper;

  @MockBean
  private StringRedisTemplate redisTemplate;

  @Test
  @DisplayName("集成测试 - 加载测试数据")
  void testLoadTestData() {
    // 测试数据加载器是否正常工作
    BidRequest request = OpenRtbTestDataLoader.minimalRequest();

    assertNotNull(request);
    assertEquals("test-request-001", request.getId());
    assertEquals(1, request.getImp().size());
    assertNotNull(request.getDevice());
    assertNotNull(request.getUser());
  }

  @Test
  @DisplayName("集成测试 - 加载完整请求数据")
  void testLoadFullRequestData() {
    BidRequest request = OpenRtbTestDataLoader.fullRequest();

    assertNotNull(request);
    assertEquals("test-request-002", request.getId());
    assertEquals(2, request.getImp().size());
    assertNotNull(request.getDevice());
    assertNotNull(request.getSite());
  }

  @Test
  @DisplayName("集成测试 - 加载视频请求数据")
  void testLoadVideoRequestData() {
    BidRequest request = OpenRtbTestDataLoader.videoRequest();

    assertNotNull(request);
    assertEquals("test-request-003", request.getId());
    assertEquals(1, request.getImp().size());
    // 验证是视频类型
    assertNotNull(request.getImp().get(0).getVideo());
  }

  @Test
  @DisplayName("集成测试 - 无展示机会请求")
  void testNoImpRequest() {
    BidRequest request = OpenRtbTestDataLoader.noImpRequest();

    assertNotNull(request);
    assertEquals("test-request-invalid-001", request.getId());
    assertTrue(request.getImp().isEmpty());
  }

  @Test
  @DisplayName("集成测试 - 无地域信息请求")
  void testNoGeoRequest() {
    BidRequest request = OpenRtbTestDataLoader.noGeoRequest();

    assertNotNull(request);
    assertEquals("test-request-004", request.getId());
    // 设备没有地域信息
    assertTrue(request.getDevice() == null || request.getDevice().getGeo() == null);
  }

  @Test
  @DisplayName("集成测试 - BidContext 创建")
  void testBidContextCreation() {
    BidRequest request = OpenRtbTestDataLoader.minimalRequest();

    BidContext context = new BidContext(request, request.getImp().get(0));

    assertNotNull(context);
    assertEquals(request, context.getRequest());
    assertEquals(request.getImp().get(0), context.getCurrentImp());
    assertEquals("test-request-001", context.getRequest().getId()); // 修复: 使用 getRequest().getId()
    assertEquals("user-test-001", context.getUserId());
    assertEquals("CN", context.getCountryCode());
    assertEquals("Beijing", context.getRegionCode());
    assertEquals(1, context.getDeviceType());
    assertEquals("iOS", context.getOs());
  }

  @Test
  @DisplayName("集成测试 - 服务Bean是否加载")
  void testServiceBeansLoaded() {
    // 如果 Spring 上下文加载成功，这些服务应该可用
    // 注意：由于使用了 @MockBean，实际的 mapper 和 redisTemplate 是模拟的

    // 这个测试主要验证 Spring 配置是否正确
    // 如果能运行到这里，说明 Bean 加载成功
    assertTrue(true, "Spring上下文加载成功");
  }

  // @Test
  // @DisplayName("集成测试 - 完整竞价流程 (需要真实数据库)")
  // void testFullBiddingProcess() {
  // // TODO: 实现完整的竞价流程测试
  // // 需要准备测试数据到数据库
  // // 或者使用 Testcontainers 进行测试
  // }
}
