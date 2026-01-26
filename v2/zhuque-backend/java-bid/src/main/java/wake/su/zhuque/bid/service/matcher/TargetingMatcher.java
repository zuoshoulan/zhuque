package wake.su.zhuque.bid.service.matcher;

import wake.su.zhuque.bid.context.BidContext;
import wake.su.zhuque.model.entity.RtbAdGroupDO;

/**
 * 定向匹配器接口
 * 处理各种定向条件的匹配逻辑
 *
 * @author zhuque
 * @version 1.0
 */
public interface TargetingMatcher {

    /**
     * 匹配检查
     *
     * @param context 竞价上下文
     * @param adGroup 广告组
     * @return true=匹配, false=不匹配
     */
    boolean matches(BidContext context, RtbAdGroupDO adGroup);
}
