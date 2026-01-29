package wake.su.zhuque.bid.service.impl.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import wake.su.zhuque.bid.context.BidContext;
import wake.su.zhuque.bid.service.filter.BidFilter;
import wake.su.zhuque.bid.service.frequency.FrequencyCapService;
import wake.su.zhuque.model.entity.RtbAdGroupDO;

import lombok.RequiredArgsConstructor;

/**
 * 频次过滤器 预检查频次是否超限（只查不累加）
 *
 * @author zhuque
 * @version 1.0
 */
@Component
@Order(4)
@RequiredArgsConstructor
public class FrequencyFilter implements BidFilter {

  private final FrequencyCapService frequencyCapService;

  @Override
  public boolean test(BidContext context, RtbAdGroupDO adGroup) {
    String userId = context.getUserId();
    if (userId == null || userId.isEmpty()) {
      // 没有用户ID时，跳过频次检查
      return true;
    }
    return frequencyCapService.checkFrequency(userId, adGroup);
  }

  @Override
  public int order() {
    return 4;
  }
}
