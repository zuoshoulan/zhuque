package wake.su.zhuque.bid.service.impl.pacing;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import wake.su.zhuque.bid.service.pacing.PacingService;
import wake.su.zhuque.model.entity.RtbAdGroupDO;

import jakarta.annotation.PostConstruct;

/**
 * 投放节奏控制服务实现
 *
 * <p>使用内存存储投放节奏数据，适合单机部署。
 * 多机部署时建议改用 Redis 实现。
 *
 * @author zhuque
 * @version 2.0
 */
@Service
public class PacingServiceImpl implements PacingService {

  private static final Logger log = LoggerFactory.getLogger(PacingServiceImpl.class);

  /** 投放节奏数据（按广告组ID） */
  private final ConcurrentHashMap<Long, PacingData> pacingMap = new ConcurrentHashMap<>();

  /** 每小时整点清理过期数据 */
  @PostConstruct
  public void init() {
    log.info("投放节奏控制服务初始化完成");
  }

  @Override
  public boolean allowBid(RtbAdGroupDO adGroup) {
    // 只在均匀投放模式下生效
    Integer deliveryMode = adGroup.getDeliveryMode();
    if (deliveryMode == null || deliveryMode != 2) {
      // 非均匀投放模式，不限制
      return true;
    }

    Integer deliveryPace = adGroup.getDeliveryPace();
    if (deliveryPace == null || deliveryPace <= 0) {
      // 未设置投放节奏，使用默认值
      deliveryPace = 10;
    }

    PacingData data = pacingMap.computeIfAbsent(adGroup.getId(), k -> new PacingData());

    // 计算距上次出价的毫秒数
    long now = System.currentTimeMillis();
    long lastBidTime = data.lastBidTime.get();
    long elapsed = now - lastBidTime;

    // deliveryPace 表示每分钟最多出价次数
    // 计算最小间隔（毫秒）= 60000 / deliveryPace
    long minInterval = 60000L / deliveryPace;

    // 首次出价或距离上次出价已超过最小间隔，允许出价
    if (lastBidTime == 0 || elapsed >= minInterval) {
      return true;
    }

    // 距离上次出价太近，跳过本次竞价
    log.debug("投放节奏控制: 广告组 {} 距离上次出价仅 {}ms，小于最小间隔 {}ms，跳过",
        adGroup.getId(), elapsed, minInterval);
    return false;
  }

  @Override
  public void recordBid(RtbAdGroupDO adGroup) {
    PacingData data = pacingMap.computeIfAbsent(adGroup.getId(), k -> new PacingData());
    data.lastBidTime.set(System.currentTimeMillis());
    data.hourlyCount.incrementAndGet();
    log.debug("投放节奏控制: 广告组 {} 记录出价，本小时第 {} 次",
        adGroup.getId(), data.hourlyCount.get());
  }

  @Override
  public int getBidCount(Long adGroupId) {
    PacingData data = pacingMap.get(adGroupId);
    return data != null ? data.hourlyCount.get() : 0;
  }

  /** 每小时清理一次计数器（保留最后出价时间） */
  @Scheduled(cron = "0 0 * * * ?")
  public void resetHourlyCounters() {
    log.info("投放节奏控制: 重置小时计数器");
    pacingMap.values().forEach(data -> data.hourlyCount.set(0));
  }

  /** 每天凌晨清理所有数据 */
  @Scheduled(cron = "0 0 0 * * ?")
  public void dailyCleanup() {
    log.info("投放节奏控制: 执行每日清理，清理前数据量: {}", pacingMap.size());
    pacingMap.clear();
  }

  /** 投放节奏数据 */
  private static class PacingData {
    /** 最后一次出价时间（毫秒时间戳） */
    final AtomicLong lastBidTime = new AtomicLong(0);

    /** 当前小时的出价次数 */
    final AtomicInteger hourlyCount = new AtomicInteger(0);
  }
}
