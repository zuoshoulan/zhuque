package wake.su.zhuque.bid.service.cache;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import wake.su.zhuque.bid.config.CacheConfig;
import wake.su.zhuque.dao.mapper.RtbCreativeMapper;
import wake.su.zhuque.model.entity.RtbCreativeDO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 创意缓存服务
 *
 * <p>负责缓存 RTB 竞价所需的创意数据
 *
 * <p>缓存策略：
 * <ul>
 * <li>缓存名称：{@link CacheConfig#CREATIVE_CACHE}</li>
 * <li>过期时间：30 秒</li>
 * <li>刷新策略：write-after-write</li>
 * </ul>
 *
 * @author zhuque
 * @since 2025-01-29
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreativeCacheService {

  private final RtbCreativeMapper creativeMapper;

  private static final Integer STATUS_ACTIVE = 1; // 进行中

  /**
   * 根据 ID 获取创意（带缓存）
   *
   * @param creativeId
   *          创意 ID
   * @return 创意对象，如果不存在返回 null
   */
  @Cacheable(value = CacheConfig.CREATIVE_CACHE, key = "#creativeId")
  public RtbCreativeDO getCreativeById(Long creativeId) {
    log.debug("缓存未命中，从数据库加载创意, id={}", creativeId);

    return creativeMapper.selectOne(
        new LambdaQueryWrapper<RtbCreativeDO>()
            .eq(RtbCreativeDO::getId, creativeId)
            .eq(RtbCreativeDO::getStatus, STATUS_ACTIVE));
  }

  /**
   * 刷新创意缓存
   *
   * <p>调用场景：
   * <ul>
   * <li>创意内容变更</li>
   * <li>创意状态变更</li>
   * <li>素材文件更新</li>
   * </ul>
   *
   * @param creativeId
   *          创意 ID
   */
  @CacheEvict(value = CacheConfig.CREATIVE_CACHE, key = "#creativeId")
  public void evictCreativeCache(Long creativeId) {
    log.info("创意缓存已清空, creativeId={}", creativeId);
  }

  /**
   * 刷新所有创意缓存
   *
   * <p>调用场景：批量更新创意时
   */
  @CacheEvict(value = CacheConfig.CREATIVE_CACHE, allEntries = true)
  public void evictAllCreativeCache() {
    log.info("所有创意缓存已清空");
  }
}
