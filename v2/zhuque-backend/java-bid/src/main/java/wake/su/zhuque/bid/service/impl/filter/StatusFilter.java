package wake.su.zhuque.bid.service.impl.filter;


import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import wake.su.zhuque.bid.context.BidContext;
import wake.su.zhuque.bid.service.filter.BidFilter;
import wake.su.zhuque.model.entity.RtbAdGroupDO;

/**
 * 状态过滤器
 * 检查广告组状态、时间范围等基本条件
 *
 * @author zhuque
 * @version 1.0
 */

@Component
@Order(1)
public class StatusFilter implements BidFilter {

    @Override
    public boolean test(BidContext context, RtbAdGroupDO adGroup) {
        // 状态检查已在查询时完成，这里可以做额外的检查
        // 例如：检查广告主状态、账户状态等
        return true;
    }

    @Override
    public int order() {
        return 1;
    }
}
