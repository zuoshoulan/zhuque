package wake.su.zhuque.bid.service.cache;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import wake.su.zhuque.bid.config.CacheConfig;
import wake.su.zhuque.bid.context.BidCandidate;
import wake.su.zhuque.dao.mapper.RtbAdGroupMapper;
import wake.su.zhuque.dao.mapper.RtbAdMapper;
import wake.su.zhuque.dao.mapper.RtbCampaignMapper;
import wake.su.zhuque.model.entity.RtbAdDO;
import wake.su.zhuque.model.entity.RtbAdGroupDO;
import wake.su.zhuque.model.entity.RtbCampaignDO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 候选广告组缓存服务
 *
 * <p>负责缓存和管理 RTB 竞价所需的候选数据：
 * <ul>
 * <li>Campaign（投放活动）</li>
 * <li>AdGroup（广告组）</li>
 * <li>Ad（广告）</li>
 * </ul>
 *
 * <p>缓存策略：
 * <ul>
 * <li>缓存名称：{@link CacheConfig#CANDIDATE_CACHE}</li>
 * <li>刷新时间：10 秒（refresh-after-write，后台刷新不阻塞请求）</li>
 * <li>数据变化时记录 info 日志</li>
 * </ul>
 *
 * @author zhuque
 * @since 2025-01-29
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CandidateCacheService {

  private final RtbCampaignMapper campaignMapper;
  private final RtbAdGroupMapper adGroupMapper;
  private final RtbAdMapper adMapper;
  private final CacheManager cacheManager;

  private static final Integer STATUS_ACTIVE = 1; // 进行中
  private static final String CACHE_KEY = "all";

  /**
   * 获取所有活跃的候选广告组（带缓存）
   *
   * <p>缓存键固定为 "all"，因为缓存的是全部活跃数据
   *
   * @return 候选广告组列表
   */
  public List<BidCandidate> getAllActiveCandidates() {
    Cache cache = cacheManager.getCache(CacheConfig.CANDIDATE_CACHE);
    if (cache == null) {
      log.error("缓存 {} 未找到", CacheConfig.CANDIDATE_CACHE);
      return loadFromDb();
    }

    // 尝试从缓存获取
    Cache.ValueWrapper wrapper = cache.get(CACHE_KEY);
    @SuppressWarnings("unchecked")
    List<BidCandidate> cached = wrapper != null ? (List<BidCandidate>) wrapper.get() : null;

    // 从数据库加载最新数据
    List<BidCandidate> fresh = loadFromDb();

    // 缓存并记录变化
    if (cached == null) {
      log.info("候选缓存初始化: 加载 {} 个活跃广告组", fresh.size());
    } else if (!candidatesEqual(cached, fresh)) {
      logChange(cached, fresh);
    }

    cache.put(CACHE_KEY, fresh);
    return fresh;
  }

  /**
   * 从数据库加载候选数据
   */
  private List<BidCandidate> loadFromDb() {
    // 获取当前日期
    LocalDate now = LocalDate.now();

    // 1. 查询所有进行中的 Campaign
    List<RtbCampaignDO> campaigns = campaignMapper.selectList(
        new LambdaQueryWrapper<RtbCampaignDO>()
            .eq(RtbCampaignDO::getStatus, STATUS_ACTIVE)
            .le(RtbCampaignDO::getStartTime, now)
            .ge(RtbCampaignDO::getEndTime, now));

    if (campaigns.isEmpty()) {
      return List.of();
    }

    List<Long> campaignIds = campaigns.stream().map(RtbCampaignDO::getId).toList();

    // 2. 查询这些 Campaign 下所有进行中的 AdGroup
    List<RtbAdGroupDO> adGroups = adGroupMapper.selectList(
        new LambdaQueryWrapper<RtbAdGroupDO>()
            .in(RtbAdGroupDO::getCampaignId, campaignIds)
            .eq(RtbAdGroupDO::getStatus, STATUS_ACTIVE));

    if (adGroups.isEmpty()) {
      return List.of();
    }

    // 3. 查询每个 AdGroup 对应的 Ad
    List<Long> adGroupIds = adGroups.stream().map(RtbAdGroupDO::getId).toList();

    List<RtbAdDO> ads = adMapper.selectList(
        new LambdaQueryWrapper<RtbAdDO>()
            .in(RtbAdDO::getAdGroupId, adGroupIds)
            .eq(RtbAdDO::getStatus, STATUS_ACTIVE));

    // 4. 组装候选对象
    Map<Long, List<RtbAdDO>> adMap = ads.stream()
        .collect(Collectors.groupingBy(RtbAdDO::getAdGroupId));

    List<BidCandidate> candidates = new ArrayList<>();
    for(RtbAdGroupDO adGroup : adGroups) {
      List<RtbAdDO> groupAds = adMap.get(adGroup.getId());
      if (groupAds != null && !groupAds.isEmpty()) {
        candidates.add(new BidCandidate(adGroup, groupAds.get(0)));
      }
    }

    return candidates;
  }

  /**
   * 比较两组候选是否相同（基于 adGroupId）
   */
  private boolean candidatesEqual(List<BidCandidate> c1, List<BidCandidate> c2) {
    if (c1.size() != c2.size()) {
      return false;
    }
    Set<Long> ids1 = c1.stream().map(c -> c.getAdGroup().getId()).collect(Collectors.toSet());
    Set<Long> ids2 = c2.stream().map(c -> c.getAdGroup().getId()).collect(Collectors.toSet());
    return ids1.equals(ids2);
  }

  /**
   * 记录缓存变化日志
   */
  private void logChange(List<BidCandidate> oldCandidates, List<BidCandidate> newCandidates) {
    Set<Long> oldIds = oldCandidates.stream().map(c -> c.getAdGroup().getId()).collect(Collectors.toSet());
    Set<Long> newIds = newCandidates.stream().map(c -> c.getAdGroup().getId()).collect(Collectors.toSet());

    Set<Long> added = new HashSet<>(newIds);
    added.removeAll(oldIds);

    Set<Long> removed = new HashSet<>(oldIds);
    removed.removeAll(newIds);

    if (!added.isEmpty() || !removed.isEmpty()) {
      log.info("候选缓存已更新: 新增 {} 个, 移除 {} 个, 当前总数 {}",
          added.size(), removed.size(), newCandidates.size());
      if (!added.isEmpty()) {
        log.debug("新增 AdGroup IDs: {}", added);
      }
      if (!removed.isEmpty()) {
        log.debug("移除 AdGroup IDs: {}", removed);
      }
    }
  }

  /**
   * 刷新候选数据缓存
   *
   * <p>调用场景：
   * <ul>
   * <li>Campaign 状态变更</li>
   * <li>AdGroup 状态变更</li>
   * <li>Ad 状态变更</li>
   * <li>预算调整</li>
   * </ul>
   */
  public void evictCandidateCache() {
    Cache cache = cacheManager.getCache(CacheConfig.CANDIDATE_CACHE);
    if (cache != null) {
      cache.evict(CACHE_KEY);
      log.info("候选数据缓存已清空，下次访问将重新加载");
    }
  }

  /**
   * 根据 ID 列表获取 AdGroup（带缓存）
   *
   * @param adGroupIds
   *          AdGroup ID 列表
   * @return AdGroup 列表
   */
  @Cacheable(value = CacheConfig.AD_GROUP_CACHE, key = "#root.target.createKey(#adGroupIds)")
  public List<RtbAdGroupDO> getAdGroupsByIds(List<Long> adGroupIds) {
    log.debug("缓存未命中，从数据库加载 AdGroup, ids={}", adGroupIds);
    return adGroupMapper.selectList(
        new LambdaQueryWrapper<RtbAdGroupDO>()
            .in(RtbAdGroupDO::getId, adGroupIds)
            .eq(RtbAdGroupDO::getStatus, STATUS_ACTIVE));
  }

  /**
   * 根据 ID 列表获取 Ads（带缓存）
   *
   * @param adIds
   *          Ad ID 列表
   * @return Ad 列表
   */
  @Cacheable(value = CacheConfig.AD_CACHE, key = "#root.target.createKey(#adIds)")
  public List<RtbAdDO> getAdsByIds(List<Long> adIds) {
    log.debug("缓存未命中，从数据库加载 Ads, ids={}", adIds);
    return adMapper.selectList(
        new LambdaQueryWrapper<RtbAdDO>()
            .in(RtbAdDO::getId, adIds)
            .eq(RtbAdDO::getStatus, STATUS_ACTIVE));
  }

  /**
   * 创建缓存键的辅助方法
   *
   * @param ids
   *          ID 列表
   * @return 缓存键
   */
  public String createKey(List<Long> ids) {
    return ids.stream()
        .map(String::valueOf)
        .sorted()
        .collect(Collectors.joining(","));
  }
}
