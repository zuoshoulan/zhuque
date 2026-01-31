package wake.su.zhuque.bid.service.impl.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import wake.su.zhuque.bid.context.BidContext;
import wake.su.zhuque.bid.service.filter.BidFilter;
import wake.su.zhuque.bid.service.pacing.PacingService;
import wake.su.zhuque.model.entity.RtbAdGroupDO;

import lombok.RequiredArgsConstructor;

/**
 * 投放节奏过滤器
 *
 * <p>在均匀投放模式下，控制广告组的出价节奏，防止预算在短时间内消耗完毕。
 *
 * @author zhuque
 * @version 2.0
 */
@Component
@Order(4)
@RequiredArgsConstructor
public class PacingFilter implements BidFilter {

  private final PacingService pacingService;

  @Override
  public boolean test(BidContext context, RtbAdGroupDO adGroup) {
    return pacingService.allowBid(adGroup);
  }

  @Override
  public int order() {
    return 4;
  }
}
