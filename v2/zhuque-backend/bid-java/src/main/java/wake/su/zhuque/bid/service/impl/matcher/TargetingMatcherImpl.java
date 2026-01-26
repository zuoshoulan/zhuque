package wake.su.zhuque.bid.service.impl.matcher;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import wake.su.zhuque.bid.context.BidContext;
import wake.su.zhuque.bid.service.matcher.GeoMatcher;
import wake.su.zhuque.bid.service.matcher.DeviceMatcher;
import wake.su.zhuque.bid.service.matcher.ScheduleMatcher;
import wake.su.zhuque.bid.service.matcher.TargetingMatcher;
import wake.su.zhuque.model.entity.RtbAdGroupDO;

/**
 * 定向匹配器实现
 * 组合各种定向条件的匹配逻辑
 *
 * @author zhuque
 * @version 1.0
 */

@Service
@RequiredArgsConstructor
public class TargetingMatcherImpl implements TargetingMatcher {

    private static final Logger log = LoggerFactory.getLogger(TargetingMatcherImpl.class);

    private final GeoMatcher geoMatcher;
    private final DeviceMatcher deviceMatcher;
    private final ScheduleMatcher scheduleMatcher;

    @Override
    public boolean matches(BidContext context, RtbAdGroupDO adGroup) {
        // 所有定向条件都满足才算匹配

        // 1. 地域匹配
        if (!geoMatcher.matches(context, adGroup)) {
            log.debug("地域不匹配, adGroup={}", adGroup.getId());
            return false;
        }

        // 2. 设备/OS匹配
        if (!deviceMatcher.matches(context, adGroup)) {
            log.debug("设备不匹配, adGroup={}", adGroup.getId());
            return false;
        }

        // 3. 时段匹配
        if (!scheduleMatcher.matches(context, adGroup)) {
            log.debug("时段不匹配, adGroup={}", adGroup.getId());
            return false;
        }

        // TODO: 人群包、关键词等更多定向条件

        return true;
    }
}
