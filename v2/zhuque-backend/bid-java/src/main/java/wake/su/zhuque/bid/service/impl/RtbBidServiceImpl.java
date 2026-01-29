package wake.su.zhuque.bid.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import wake.su.zhuque.bid.context.BidCandidate;
import wake.su.zhuque.bid.context.BidContext;
import wake.su.zhuque.bid.dto.openrtb.Bid;
import wake.su.zhuque.bid.dto.openrtb.BidRequest;
import wake.su.zhuque.bid.dto.openrtb.BidResponse;
import wake.su.zhuque.bid.dto.openrtb.Imp;
import wake.su.zhuque.bid.dto.openrtb.SeatBid;
import wake.su.zhuque.bid.service.RtbBidService;
import wake.su.zhuque.bid.service.budget.BudgetControlService;
import wake.su.zhuque.bid.service.creative.CreativeAssemblyService;
import wake.su.zhuque.bid.service.filter.BidFilter;
import wake.su.zhuque.bid.service.frequency.FrequencyCapService;
import wake.su.zhuque.bid.service.pricing.BidPriceService;
import wake.su.zhuque.dao.mapper.RtbAdGroupMapper;
import wake.su.zhuque.dao.mapper.RtbAdMapper;
import wake.su.zhuque.dao.mapper.RtbCampaignMapper;
import wake.su.zhuque.model.entity.RtbAdDO;
import wake.su.zhuque.model.entity.RtbAdGroupDO;
import wake.su.zhuque.model.entity.RtbCampaignDO;
import wake.su.zhuque.model.entity.RtbCreativeDO;

import lombok.RequiredArgsConstructor;

/**
 * RTB 竞价核心服务实现
 *
 * @author zhuque
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class RtbBidServiceImpl implements RtbBidService {

  private static final Logger log = LoggerFactory.getLogger(RtbBidServiceImpl.class);

  private final RtbCampaignMapper campaignMapper;
  private final RtbAdGroupMapper adGroupMapper;
  private final RtbAdMapper adMapper;
  private final List<BidFilter> bidFilters;
  private final BidPriceService bidPriceService;
  private final BudgetControlService budgetControlService;
  private final FrequencyCapService frequencyCapService;
  private final CreativeAssemblyService creativeAssemblyService;

  private static final Integer STATUS_ACTIVE = 1; // 进行中

  @Override
  public BidResponse processBid(BidRequest request) {
    long startTime = System.currentTimeMillis();
    String requestId = request.getId();

    log.debug("[{}] 开始处理竞价请求, impCount={}", requestId, request.getImp().size());

    // 如果没有展示机会，直接返回
    if (request.getImp() == null || request.getImp().isEmpty()) {
      log.warn("[{}] 请求中没有展示机会", requestId);
      return null;
    }

    // 遍历所有展示机会，为每个符合条件的 imp 生成竞价
    List<Bid> winningBids = new ArrayList<>();
    int successCount = 0;
    int failedCount = 0;

    for(Imp imp : request.getImp()) {
      BidResponse impResponse = processSingleImp(request, imp);
      if (impResponse != null && impResponse.getSeatbid() != null
          && !impResponse.getSeatbid().isEmpty()) {
        // 提取 bid 并添加到结果列表
        SeatBid seatBid = impResponse.getSeatbid().get(0);
        if (seatBid.getBid() != null && !seatBid.getBid().isEmpty()) {
          winningBids.addAll(seatBid.getBid());
          successCount++;
        } else {
          failedCount++;
        }
      } else {
        failedCount++;
      }
    }

    // 如果没有任何竞价成功，返回 null
    if (winningBids.isEmpty()) {
      log.debug("[{}] 所有展示机会均未产生竞价", requestId);
      return null;
    }

    // 构造最终响应，包含所有成功的竞价
    BidResponse response = new BidResponse();
    response.setId(request.getId());

    SeatBid seatBid = new SeatBid();
    seatBid.setBid(winningBids);
    response.setSeatbid(Arrays.asList(seatBid));

    long duration = System.currentTimeMillis() - startTime;
    log.info("[{}] 竞价处理完成, success={}, failed={}, duration={}ms", requestId, successCount,
        failedCount, duration);

    return response;
  }

  /**
   * 处理单个展示机会的竞价
   *
   * @param request
   *          竞价请求
   * @param imp
   *          单个展示机会
   * @return 竞价响应（仅包含该 imp 的竞价），如果没有合适的竞价则返回 null
   */
  private BidResponse processSingleImp(BidRequest request, Imp imp) {
    String requestId = request.getId();
    String impId = imp.getId();

    log.debug("[{}] 处理展示机会: {}", requestId, impId);

    // 构建上下文
    BidContext context = new BidContext(request, imp);

    // 1. 获取候选广告组
    List<BidCandidate> candidates = getCandidates(context);
    if (candidates.isEmpty()) {
      log.debug("[{}] 展示机会 {} 没有候选广告组", requestId, impId);
      return null;
    }

    // 2. 预过滤阶段 (轻量级检查)
    List<BidCandidate> passedCandidates = preFilter(context, candidates);
    if (passedCandidates.isEmpty()) {
      log.debug("[{}] 展示机会 {} 预过滤后无候选广告组", requestId, impId);
      return null;
    }

    // 3. 计算出价和分数
    for(BidCandidate candidate : passedCandidates) {
      Long bidPrice = bidPriceService.calculateBidPrice(candidate.getAdGroup(), context);
      candidate.setBidPrice(bidPrice);
      candidate.calculateScore();
    }

    // 4. 按分数排序
    passedCandidates.sort(Comparator.naturalOrder());

    // 5. 遍历排序后的候选，尝试扣资源
    BidCandidate winner = trySelectWinner(context, passedCandidates);

    if (winner == null) {
      log.debug("[{}] 展示机会 {} 所有候选广告组资源扣减失败", requestId, impId);
      return null;
    }

    // 6. 构造响应
    BidResponse response = buildResponse(request, imp, winner, context);

    log.info("[{}] 展示机会 {} 竞价成功, adGroupId={}, bidPrice={}", requestId, impId,
        winner.getAdGroup().getId(), winner.getBidPrice());

    return response;
  }

  /** 获取候选广告组 */
  private List<BidCandidate> getCandidates(BidContext context) {
    // 查询所有进行中的 Campaign
    List<RtbCampaignDO> campaigns = campaignMapper.selectList(
        new LambdaQueryWrapper<RtbCampaignDO>().eq(RtbCampaignDO::getStatus, STATUS_ACTIVE)
            .le(RtbCampaignDO::getStartTime, context.getNow())
            .ge(RtbCampaignDO::getEndTime, context.getNow()));

    if (campaigns.isEmpty()) {
      return List.of();
    }

    List<Long> campaignIds = campaigns.stream().map(RtbCampaignDO::getId).toList();

    // 查询这些 Campaign 下所有进行中的 AdGroup
    // Note: AdGroup 没有 startTime/endTime 字段，时间控制由 Campaign 统一管理
    List<RtbAdGroupDO> adGroups = adGroupMapper.selectList(new LambdaQueryWrapper<RtbAdGroupDO>()
        .in(RtbAdGroupDO::getCampaignId, campaignIds).eq(RtbAdGroupDO::getStatus, STATUS_ACTIVE));

    if (adGroups.isEmpty()) {
      return List.of();
    }

    // 查询每个 AdGroup 对应的 Ad
    List<Long> adGroupIds = adGroups.stream().map(RtbAdGroupDO::getId).toList();

    List<RtbAdDO> ads = adMapper.selectList(new LambdaQueryWrapper<RtbAdDO>()
        .in(RtbAdDO::getAdGroupId, adGroupIds).eq(RtbAdDO::getStatus, STATUS_ACTIVE));

    // 组装候选对象
    List<BidCandidate> candidates = new ArrayList<>();
    for(RtbAdGroupDO adGroup : adGroups) {
      for(RtbAdDO ad : ads) {
        if (ad.getAdGroupId().equals(adGroup.getId())) {
          candidates.add(new BidCandidate(adGroup, ad));
          break; // 每个 AdGroup 只取一个 Ad
        }
      }
    }

    return candidates;
  }

  /** 预过滤阶段 - 轻量级检查 */
  private List<BidCandidate> preFilter(BidContext context, List<BidCandidate> candidates) {
    List<BidCandidate> passed = new ArrayList<>();

    // 按顺序执行过滤器
    List<BidFilter> sortedFilters = bidFilters.stream().sorted(Comparator.comparingInt(BidFilter::order)).toList();

    for(BidCandidate candidate : candidates) {
      RtbAdGroupDO adGroup = candidate.getAdGroup();

      boolean allPassed = true;
      for(BidFilter filter : sortedFilters) {
        if (!filter.test(context, adGroup)) {
          allPassed = false;
          break;
        }
      }

      if (allPassed) {
        passed.add(candidate);
      }
    }

    return passed;
  }

  /** 尝试选择获胜者 - 扣资源阶段 */
  private BidCandidate trySelectWinner(BidContext context, List<BidCandidate> candidates) {
    BigDecimal bidPrice = BigDecimal.valueOf(candidates.get(0).getBidPrice()).divide(BigDecimal.valueOf(1000));

    for(BidCandidate candidate : candidates) {
      RtbAdGroupDO adGroup = candidate.getAdGroup();
      String userId = context.getUserId();

      // 1. 尝试扣减预算 (原子操作)
      if (!budgetControlService.tryDeduct(adGroup, bidPrice)) {
        log.debug("预算扣减失败, adGroup={}", adGroup.getId());
        continue;
      }

      // 2. 尝试记录频次 (原子操作)
      if (!frequencyCapService.tryRecord(userId, adGroup)) {
        log.debug("频次记录失败, adGroup={}", adGroup.getId());
        budgetControlService.rollback(adGroup, bidPrice); // 回滚预算
        continue;
      }

      // 两项都成功，选中
      candidate.setBudgetPassed(true);
      candidate.setFrequencyPassed(true);
      return candidate;
    }

    return null;
  }

  /** 构造响应 */
  private BidResponse buildResponse(BidRequest request, Imp imp, BidCandidate winner,
      BidContext context) {
    BidResponse response = new BidResponse();
    response.setId(request.getId());

    // 构建 SeatBid
    SeatBid seatBid = new SeatBid();
    seatBid.setSeat(winner.getAdGroup().getAdvertiserId().toString());

    // 构建 Bid
    Bid bid = new Bid();
    bid.setId(imp.getId());
    bid.setPrice(BigDecimal.valueOf(winner.getBidPrice()));
    bid.setAdId(winner.getAd().getId().toString());
    bid.setCreativeId(winner.getAd().getCreativeId().toString());
    bid.setCampaignId(winner.getAdGroup().getCampaignId().toString());

    // 生成 ADM
    RtbCreativeDO creative = getCreative(winner.getAd().getCreativeId());
    if (creative != null) {
      String adm = creativeAssemblyService.buildAdm(winner.getAd(), creative, context);
      bid.setAdm(adm);
    }

    seatBid.setBid(Arrays.asList(bid));
    response.setSeatbid(Arrays.asList(seatBid));

    return response;
  }

  /** 获取创意 (简化实现，后续从缓存或服务获取) */
  private RtbCreativeDO getCreative(Long creativeId) {
    // TODO: 实现从 service 获取创意
    return null;
  }
}
