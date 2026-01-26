package wake.su.zhuque.bid.service.creative;

import wake.su.zhuque.bid.context.BidContext;
import wake.su.zhuque.model.entity.RtbAdDO;
import wake.su.zhuque.model.entity.RtbCreativeDO;

/**
 * 创意组装服务
 * 根据广告和创意生成 ADM (Ad Markup)
 *
 * @author zhuque
 * @version 1.0
 */
public interface CreativeAssemblyService {

    /**
     * 构造 ADM (创意内容)
     *
     * @param ad       广告
     * @param creative 创意
     * @param context  竞价上下文
     * @return ADM 字符串 (HTML/VAST XML/Native JSON)
     */
    String buildAdm(RtbAdDO ad, RtbCreativeDO creative, BidContext context);

    /**
     * 拼接点击追踪链接
     *
     * @param baseUrl  基础URL
     * @param context  竞价上下文
     * @return 完整的点击链接
     */
    String buildClickUrl(String baseUrl, BidContext context);

    /**
     * 拼接展示追踪链接
     *
     * @param baseUrl  基础URL
     * @param context  竞价上下文
     * @return 完整的展示追踪链接
     */
    String buildImpressionUrl(String baseUrl, BidContext context);

    /**
     * 拼接赢拍通知链接
     *
     * @param baseUrl  基础URL
     * @param context  竞价上下文
     * @param price    赢拍价格
     * @return 完整的赢拍通知链接
     */
    String buildWinUrl(String baseUrl, BidContext context, Long price);
}
