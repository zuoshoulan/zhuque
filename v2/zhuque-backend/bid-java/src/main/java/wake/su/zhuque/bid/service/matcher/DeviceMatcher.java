package wake.su.zhuque.bid.service.matcher;

import wake.su.zhuque.bid.context.BidContext;
import wake.su.zhuque.model.entity.RtbAdGroupDO;

/**
 * 设备匹配器接口
 *
 * @author zhuque
 * @version 1.0
 */
public interface DeviceMatcher extends TargetingMatcher {

  /**
   * 检查设备/OS是否匹配
   *
   * @param context
   *          竞价上下文
   * @param adGroup
   *          广告组
   * @return true=匹配, false=不匹配
   */
  boolean matches(BidContext context, RtbAdGroupDO adGroup);
}
