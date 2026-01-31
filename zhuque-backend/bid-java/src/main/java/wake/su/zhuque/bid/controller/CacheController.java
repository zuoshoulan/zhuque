package wake.su.zhuque.bid.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wake.su.zhuque.bid.service.cache.CandidateCacheService;
import wake.su.zhuque.bid.service.cache.CreativeCacheService;
import wake.su.zhuque.common.core.result.Result;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 缓存管理控制器
 *
 * <p>提供手动刷新缓存的 API 接口
 *
 * <p>使用场景：
 * <ul>
 * <li>Campaign/AdGroup/Ad 数据变更后刷新候选缓存</li>
 * <li>Creative 数据变更后刷新创意缓存</li>
 * <li>批量数据更新后刷新所有缓存</li>
 * </ul>
 *
 * @author zhuque
 * @since 2025-01-29
 */
@Slf4j
@Tag(name = "缓存管理", description = "RTB 竞价服务缓存管理接口")
@RestController
@RequestMapping("/api/cache")
@RequiredArgsConstructor
public class CacheController {

  private final CandidateCacheService candidateCacheService;
  private final CreativeCacheService creativeCacheService;

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
  @PostMapping("/candidates/refresh")
  @Operation(summary = "刷新候选数据缓存", description = "清空候选数据缓存，下次访问时重新从数据库加载")
  public ResponseEntity<Result<Void>> refreshCandidateCache() {
    log.info("手动刷新候选数据缓存");
    candidateCacheService.evictCandidateCache();
    return ResponseEntity.ok(Result.success());
  }

  /**
   * 刷新指定创意缓存
   *
   * @param creativeId
   *          创意 ID
   */
  @PostMapping("/creatives/{creativeId}/refresh")
  @Operation(summary = "刷新指定创意缓存", description = "清空指定创意的缓存，下次访问时重新从数据库加载")
  public ResponseEntity<Result<Void>> refreshCreativeCache(@PathVariable Long creativeId) {
    log.info("手动刷新创意缓存, creativeId={}", creativeId);
    creativeCacheService.evictCreativeCache(creativeId);
    return ResponseEntity.ok(Result.success());
  }

  /**
   * 刷新所有创意缓存
   *
   * <p>调用场景：批量更新创意时
   */
  @PostMapping("/creatives/refresh-all")
  @Operation(summary = "刷新所有创意缓存", description = "清空所有创意缓存")
  public ResponseEntity<Result<Void>> refreshAllCreativeCache() {
    log.info("手动刷新所有创意缓存");
    creativeCacheService.evictAllCreativeCache();
    return ResponseEntity.ok(Result.success());
  }

  /**
   * 刷新所有缓存
   *
   * <p>调用场景：批量数据更新或需要确保缓存完全刷新时
   */
  @PostMapping("/refresh-all")
  @Operation(summary = "刷新所有缓存", description = "清空所有缓存（候选数据、创意等）")
  public ResponseEntity<Result<Void>> refreshAllCache() {
    log.info("手动刷新所有缓存");
    candidateCacheService.evictCandidateCache();
    creativeCacheService.evictAllCreativeCache();
    return ResponseEntity.ok(Result.success());
  }
}
