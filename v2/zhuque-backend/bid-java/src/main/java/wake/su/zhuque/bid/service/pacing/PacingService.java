package wake.su.zhuque.bid.service.pacing;

import wake.su.zhuque.model.entity.RtbAdGroupDO;

/**
 * 投放节奏控制服务
 *
 * <p>用于均匀投放模式下，控制广告组的出价节奏，防止预算在短时间内消耗完毕。
 *
 * <p>核心逻辑：
 * <ul>
 * <li>记录广告组最近的成功出价时间</li>
 * <li>根据 deliveryPace 参数计算最小出价间隔</li>
 * <li>距离上次出价太近时跳过本次竞价</li>
 * </ul>
 *
 * @author zhuque
 * @version 2.0
 */
public interface PacingService {

  /**
   * 检查是否允许出价
   *
   * @param adGroup
   *          广告组
   * @return true=允许出价, false=跳过（节奏控制）
   */
  boolean allowBid(RtbAdGroupDO adGroup);

  /**
   * 记录成功出价
   *
   * @param adGroup
   *          广告组
   */
  void recordBid(RtbAdGroupDO adGroup);

  /**
   * 获取当前小时的成功出价次数
   *
   * @param adGroupId
   *          广告组ID
   * @return 出价次数
   */
  int getBidCount(Long adGroupId);
}
