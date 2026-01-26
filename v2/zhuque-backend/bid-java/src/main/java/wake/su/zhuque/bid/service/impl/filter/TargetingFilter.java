package wake.su.zhuque.bid.service.impl.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import wake.su.zhuque.bid.context.BidContext;
import wake.su.zhuque.bid.service.filter.BidFilter;
import wake.su.zhuque.bid.service.matcher.TargetingMatcher;
import wake.su.zhuque.model.entity.RtbAdGroupDO;

/**
 * 定向过滤器
 * 检查地域、设备、时段等定向条件
 *
 * @author zhuque
 * @version 1.0
 */

@Component
@Order(2)
public class TargetingFilter implements BidFilter {

    @Autowired
    @Qualifier("targetingMatcherImpl")
    private TargetingMatcher targetingMatcher;

    @Override
    public boolean test(BidContext context, RtbAdGroupDO adGroup) {
        return targetingMatcher.matches(context, adGroup);
    }

    @Override
    public int order() {
        return 2;
    }
}
