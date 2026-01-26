package wake.su.zhuque.bid.service.impl.filter;

import java.math.BigDecimal;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import wake.su.zhuque.bid.context.BidContext;
import wake.su.zhuque.bid.service.budget.BudgetControlService;
import wake.su.zhuque.bid.service.filter.BidFilter;
import wake.su.zhuque.model.entity.RtbAdGroupDO;

import lombok.RequiredArgsConstructor;

/**
 * 预算过滤器 预检查预算是否充足（只查不扣）
 *
 * @author zhuque
 * @version 1.0
 */
@Component
@Order(3)
@RequiredArgsConstructor
public class BudgetFilter implements BidFilter {

  private final BudgetControlService budgetControlService;

  @Override
  public boolean test(BidContext context, RtbAdGroupDO adGroup) {
    // 使用一个预估出价做预检查
    BigDecimal estimatedPrice = adGroup.getBaseBidPrice();
    return budgetControlService.checkBudget(adGroup, estimatedPrice);
  }

  @Override
  public int order() {
    return 3;
  }
}
