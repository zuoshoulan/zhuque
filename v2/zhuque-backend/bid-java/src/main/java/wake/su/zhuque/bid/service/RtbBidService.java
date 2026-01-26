package wake.su.zhuque.bid.service;

import wake.su.zhuque.bid.dto.openrtb.BidRequest;
import wake.su.zhuque.bid.dto.openrtb.BidResponse;

/**
 * RTB 竞价核心服务
 *
 * @author zhuque
 * @version 1.0
 */
public interface RtbBidService {

    /**
     * 处理竞价请求
     *
     * @param request OpenRTB BidRequest
     * @return BidResponse 有竞价, null 无竞价
     */
    BidResponse processBid(BidRequest request);
}
