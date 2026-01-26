package wake.su.zhuque.bid.filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import wake.su.zhuque.bid.context.BidContext;
import wake.su.zhuque.bid.dto.openrtb.BidRequest;
import wake.su.zhuque.bid.dto.openrtb.Imp;
import wake.su.zhuque.bid.service.frequency.FrequencyCapService;
import wake.su.zhuque.bid.service.impl.filter.FrequencyFilter;
import wake.su.zhuque.model.entity.RtbAdGroupDO;

/** 频次过滤器测试 */
@ExtendWith(MockitoExtension.class)
public class FrequencyFilterTest {

  @Mock private FrequencyCapService frequencyCapService;

  private FrequencyFilter frequencyFilter;
  private BidContext context;
  private RtbAdGroupDO adGroup;

  @BeforeEach
  void setUp() {
    frequencyFilter = new FrequencyFilter(frequencyCapService);

    BidRequest request = new BidRequest();
    request.setId("test-request");

    Imp imp = new Imp();
    imp.setId("imp-001");
    request.setImp(List.of(imp));

    // 设置用户ID
    wake.su.zhuque.bid.dto.openrtb.User user = new wake.su.zhuque.bid.dto.openrtb.User();
    user.setId("test-user-001");
    request.setUser(user);

    context = new BidContext(request, imp);

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
  @DisplayName("频次未超限应通过")
  void testFrequencyNotExceeded() {
    adGroup.setFrequencyCap(10);
    adGroup.setFrequencyCapPeriod(2);

    when(frequencyCapService.checkFrequency(any(), any())).thenReturn(true);

    assertTrue(frequencyFilter.test(context, adGroup));
  }

  @Test
  @DisplayName("频次超限应失败")
  void testFrequencyExceeded() {
    adGroup.setFrequencyCap(10);
    adGroup.setFrequencyCapPeriod(2);

    when(frequencyCapService.checkFrequency(any(), any())).thenReturn(false);

    assertFalse(frequencyFilter.test(context, adGroup));
  }

  @Test
  @DisplayName("无频次限制应通过")
  void testNoFrequencyCap() {
    adGroup.setFrequencyCap(null);
    adGroup.setFrequencyCapPeriod(null);

    assertTrue(frequencyFilter.test(context, adGroup));
  }

  @Test
  @DisplayName("频次上限为0应通过")
  void testZeroFrequencyCap() {
    adGroup.setFrequencyCap(0);
    adGroup.setFrequencyCapPeriod(2);

    assertTrue(frequencyFilter.test(context, adGroup));
  }

  @Test
  @DisplayName("无用户ID时应通过")
  void testNoUserId() {
    // 创建无用户ID的上下文
    BidRequest request = new BidRequest();
    request.setId("test-request-no-user");
    request.setUser(null);

    Imp imp = new Imp();
    imp.setId("imp-001");
    request.setImp(List.of(imp));

    BidContext noUserContext = new BidContext(request, imp);

    adGroup.setFrequencyCap(10);

    // 无用户ID时跳过频次检查
    assertTrue(frequencyFilter.test(noUserContext, adGroup));
  }

  @Test
  @DisplayName("空用户ID时应通过")
  void testEmptyUserId() {
    // 创建空用户ID的上下文
    BidRequest request = new BidRequest();
    request.setId("test-request-empty-user");

    wake.su.zhuque.bid.dto.openrtb.User user = new wake.su.zhuque.bid.dto.openrtb.User();
    user.setId("");
    request.setUser(user);

    Imp imp = new Imp();
    imp.setId("imp-001");
    request.setImp(List.of(imp));

    BidContext emptyUserContext = new BidContext(request, imp);

    adGroup.setFrequencyCap(10);

    // 空用户ID时跳过频次检查
    assertTrue(frequencyFilter.test(emptyUserContext, adGroup));
  }

  @Test
  @DisplayName("过滤器顺序应为4")
  void testFilterOrder() {
    assertEquals(4, frequencyFilter.order());
  }
}
