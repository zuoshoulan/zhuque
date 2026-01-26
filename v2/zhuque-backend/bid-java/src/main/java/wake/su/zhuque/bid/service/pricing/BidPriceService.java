package wake.su.zhuque.bid.service.pricing;

import wake.su.zhuque.bid.context.BidContext;
import wake.su.zhuque.model.entity.RtbAdGroupDO;

/**
 * 出价计算服务
 *
 * @author zhuque
 * @version 1.0
 */
public interface BidPriceService {

  /**
   * 计算最终出价
   *
   * @param adGroup 广告组
   * @param context 竞价上下文
   * @return 出价（微元/千次单位）
   */
  Long calculateBidPrice(RtbAdGroupDO adGroup, BidContext context);
}
