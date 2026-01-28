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
import wake.su.zhuque.bid.service.budget.BudgetControlService;
import wake.su.zhuque.bid.service.impl.filter.BudgetFilter;
import wake.su.zhuque.model.entity.RtbAdGroupDO;

/** 预算过滤器测试 */
@ExtendWith(MockitoExtension.class)
public class BudgetFilterTest {

  @Mock
  private BudgetControlService budgetControlService;

  private BudgetFilter budgetFilter;
  private BidContext context;
  private RtbAdGroupDO adGroup;

  @BeforeEach
  void setUp() {
    budgetFilter = new BudgetFilter(budgetControlService);

    BidRequest request = new BidRequest();
    request.setId("test-request");
    Imp imp = new Imp();
    imp.setId("imp-001");
    request.setImp(List.of(imp));
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
  @DisplayName("预算充足应通过")
  void testBudgetSufficient() {
    adGroup.setDailyBudget(BigDecimal.valueOf(100));
    adGroup.setDailyBudgetUsed(BigDecimal.valueOf(50));

    when(budgetControlService.checkBudget(any(), any())).thenReturn(true);

    assertTrue(budgetFilter.test(context, adGroup));
  }

  @Test
  @DisplayName("预算耗尽应失败")
  void testBudgetExhausted() {
    adGroup.setDailyBudget(BigDecimal.valueOf(100));
    adGroup.setDailyBudgetUsed(BigDecimal.valueOf(100));

    when(budgetControlService.checkBudget(any(), any())).thenReturn(false);

    assertFalse(budgetFilter.test(context, adGroup));
  }

  @Test
  @DisplayName("无日预算限制应通过")
  void testNoDailyBudget() {
    adGroup.setDailyBudget(null);
    adGroup.setDailyBudgetUsed(null);

    // 无预算限制时应该通过
    when(budgetControlService.checkBudget(any(), any())).thenReturn(true);

    assertTrue(budgetFilter.test(context, adGroup));
  }

  @Test
  @DisplayName("过滤器顺序应为3")
  void testFilterOrder() {
    assertEquals(3, budgetFilter.order());
  }
}
