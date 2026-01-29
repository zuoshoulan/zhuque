package wake.su.zhuque.bid.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * Caffeine 缓存配置
 *
 * <p>用于缓存 RTB 竞价服务中频繁访问的数据：
 * <ul>
 * <li>投放活动 (Campaign)</li>
 * <li>广告组 (AdGroup)</li>
 * <li>广告 (Ad)</li>
 * <li>创意 (Creative)</li>
 * <li>素材 (Material)</li>
 * </ul>
 *
 * @author zhuque
 * @since 2025-01-29
 */
@Configuration
@EnableCaching
public class CacheConfig {

  /** 缓存名称常量 */
  public static final String CAMPAIGN_CACHE = "campaignCache";
  public static final String AD_GROUP_CACHE = "adGroupCache";
  public static final String AD_CACHE = "adCache";
  public static final String CREATIVE_CACHE = "creativeCache";
  public static final String MATERIAL_CACHE = "materialCache";
  public static final String CANDIDATE_CACHE = "candidateCache"; // 组合缓存：Campaign+AdGroup+Ad

  /**
   * 配置 Caffeine 缓存管理器
   *
   * <p>缓存策略：
   * <ul>
   * <li>过期时间：30 秒后自动刷新（write-after-write）</li>
   * <li>初始容量：100 个条目</li>
   * <li>最大容量：1000 个条目（基于内存使用自动清理）</li>
   * <li>统计信息：启用缓存命中率统计</li>
   * </ul>
   */
  @Bean
  public CacheManager cacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager();

    // 配置各个缓存
    cacheManager.registerCustomCache(CAMPAIGN_CACHE,
        buildCaffeine(100, 500, 30).build());
    cacheManager.registerCustomCache(AD_GROUP_CACHE,
        buildCaffeine(200, 1000, 30).build());
    cacheManager.registerCustomCache(AD_CACHE,
        buildCaffeine(200, 1000, 30).build());
    cacheManager.registerCustomCache(CREATIVE_CACHE,
        buildCaffeine(100, 500, 30).build());
    cacheManager.registerCustomCache(MATERIAL_CACHE,
        buildCaffeine(100, 500, 60).build());
    cacheManager.registerCustomCache(CANDIDATE_CACHE,
        buildCaffeine(50, 200, 10).build()); // 候选数据缓存，10秒刷新

    return cacheManager;
  }

  /**
   * 构建 Caffeine 实例
   *
   * @param initialCapacity
   *          初始容量
   * @param maximumSize
   *          最大容量
   * @param expireAfterSeconds
   *          写入后过期时间（秒）
   * @return Caffeine 实例
   */
  private Caffeine<Object, Object> buildCaffeine(int initialCapacity, int maximumSize,
      int expireAfterSeconds) {
    return Caffeine.newBuilder()
        // 初始容量
        .initialCapacity(initialCapacity)
        // 最大容量（基于权重）
        .maximumSize(maximumSize)
        // 写入后过期时间
        .expireAfterWrite(expireAfterSeconds, TimeUnit.SECONDS)
        // 启用统计
        .recordStats()
        // 移除监听器（可选，用于调试）
        .removalListener((key, value, cause) -> {
          // 可以在这里记录缓存移除事件，便于调试
          // log.debug("缓存移除: key={}, cause={}", key, cause);
        });
  }
}
