package wake.su.zhuque.bid.service.filter;

import wake.su.zhuque.bid.context.BidContext;
import wake.su.zhuque.model.entity.RtbAdGroupDO;

/**
 * 竞价过滤器接口
 * 用于责任链模式，依次过滤不匹配的广告组
 *
 * @author zhuque
 * @version 1.0
 */
public interface BidFilter {

    /**
     * 过滤检查
     *
     * @param context  竞价上下文
     * @param adGroup  待检查的广告组
     * @return true=保留, false=过滤掉
     */
    boolean test(BidContext context, RtbAdGroupDO adGroup);

    /**
     * 过滤器顺序
     * 数字越小越先执行
     *
     * @return 顺序号
     */
    int order();
}
