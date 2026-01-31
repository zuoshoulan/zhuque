package wake.su.zhuque.service.impl.permission;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * 权限缓存服务 负责用户权限的Redis缓存管理
 *
 * @author wake.su
 * @since 2026-01-10
 */
@Slf4j
@Service
public class PermissionCacheService {

  private final RedisTemplate<String, Object> redisTemplate;

  // 缓存前缀
  private static final String USER_PERMISSIONS_PREFIX = "auth:user:permissions:";
  private static final String USER_INFO_PREFIX = "auth:user:info:";

  // 缓存过期时间（分钟）
  private static final long PERMISSIONS_CACHE_TTL = 30;
  private static final long USER_INFO_CACHE_TTL = 60;

  public PermissionCacheService(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  /**
   * 缓存用户权限集合
   *
   * @param userId
   *          用户ID
   * @param permissions
   *          权限编码集合
   */
  public void cacheUserPermissions(Long userId, Set<String> permissions) {
    String key = USER_PERMISSIONS_PREFIX + userId;
    try {
      redisTemplate.opsForValue().set(key, permissions, PERMISSIONS_CACHE_TTL, TimeUnit.MINUTES);
      log.debug("缓存用户权限: userId={}, permissionsCount={}", userId, permissions.size());
    } catch(Exception e) {
      log.error("缓存用户权限失败: userId={}, error={}", userId, e.getMessage(), e);
    }
  }

  /**
   * 获取用户权限集合
   *
   * @param userId
   *          用户ID
   * @return 权限编码集合，如果不存在返回null
   */
  @SuppressWarnings("unchecked")
  public Set<String> getUserPermissions(Long userId) {
    String key = USER_PERMISSIONS_PREFIX + userId;
    try {
      Object permissions = redisTemplate.opsForValue().get(key);
      if (permissions instanceof Set) {
        log.debug("从缓存获取用户权限: userId={}, permissionsCount={}", userId, ((Set<?>) permissions).size());
        return (Set<String>) permissions;
      }
    } catch(Exception e) {
      log.error("获取用户权限缓存失败: userId={}, error={}", userId, e.getMessage(), e);
    }
    return null;
  }

  /**
   * 删除用户权限缓存
   *
   * @param userId
   *          用户ID
   */
  public void clearUserPermissions(Long userId) {
    String key = USER_PERMISSIONS_PREFIX + userId;
    try {
      redisTemplate.delete(key);
      log.info("清除用户权限缓存: userId={}", userId);
    } catch(Exception e) {
      log.error("清除用户权限缓存失败: userId={}, error={}", userId, e.getMessage(), e);
    }
  }

  /**
   * 批量清除用户权限缓存
   *
   * @param userIds
   *          用户ID集合
   */
  public void clearUserPermissionsBatch(Set<Long> userIds) {
    if (userIds == null || userIds.isEmpty()) {
      return;
    }

    try {
      Set<String> keys = userIds.stream().map(userId -> USER_PERMISSIONS_PREFIX + userId)
          .collect(java.util.stream.Collectors.toSet());

      Long deleteCount = redisTemplate.delete(keys);
      log.info("批量清除用户权限缓存: count={}", deleteCount);
    } catch(Exception e) {
      log.error("批量清除用户权限缓存失败: error={}", e.getMessage(), e);
    }
  }

  /**
   * 清除所有权限缓存 注意：此操作会清除所有用户的权限缓存，慎用
   */
  public void clearAllPermissions() {
    try {
      Set<String> keys = redisTemplate.keys(USER_PERMISSIONS_PREFIX + "*");
      if (keys != null && !keys.isEmpty()) {
        redisTemplate.delete(keys);
        log.info("清除所有权限缓存: count={}", keys.size());
      }
    } catch(Exception e) {
      log.error("清除所有权限缓存失败: error={}", e.getMessage(), e);
    }
  }

  /**
   * 检查权限缓存是否存在
   *
   * @param userId
   *          用户ID
   * @return 是否存在
   */
  public boolean hasPermissionCache(Long userId) {
    String key = USER_PERMISSIONS_PREFIX + userId;
    try {
      Boolean exists = redisTemplate.hasKey(key);
      return Boolean.TRUE.equals(exists);
    } catch(Exception e) {
      log.error("检查权限缓存失败: userId={}, error={}", userId, e.getMessage(), e);
      return false;
    }
  }

  /**
   * 刷新权限缓存过期时间
   *
   * @param userId
   *          用户ID
   */
  public void refreshPermissionCache(Long userId) {
    String key = USER_PERMISSIONS_PREFIX + userId;
    try {
      redisTemplate.expire(key, PERMISSIONS_CACHE_TTL, TimeUnit.MINUTES);
      log.debug("刷新权限缓存过期时间: userId={}", userId);
    } catch(Exception e) {
      log.error("刷新权限缓存过期时间失败: userId={}, error={}", userId, e.getMessage(), e);
    }
  }

  /**
   * 缓存用户基本信息
   *
   * @param userId
   *          用户ID
   * @param userInfo
   *          用户信息
   */
  public void cacheUserInfo(Long userId, Object userInfo) {
    String key = USER_INFO_PREFIX + userId;
    try {
      redisTemplate.opsForValue().set(key, userInfo, USER_INFO_CACHE_TTL, TimeUnit.MINUTES);
      log.debug("缓存用户信息: userId={}", userId);
    } catch(Exception e) {
      log.error("缓存用户信息失败: userId={}, error={}", userId, e.getMessage(), e);
    }
  }

  /**
   * 获取用户基本信息
   *
   * @param userId
   *          用户ID
   * @return 用户信息
   */
  public Object getUserInfo(Long userId) {
    String key = USER_INFO_PREFIX + userId;
    try {
      return redisTemplate.opsForValue().get(key);
    } catch(Exception e) {
      log.error("获取用户信息缓存失败: userId={}, error={}", userId, e.getMessage(), e);
      return null;
    }
  }

  /**
   * 清除用户基本信息缓存
   *
   * @param userId
   *          用户ID
   */
  public void clearUserInfo(Long userId) {
    String key = USER_INFO_PREFIX + userId;
    try {
      redisTemplate.delete(key);
      log.debug("清除用户信息缓存: userId={}", userId);
    } catch(Exception e) {
      log.error("清除用户信息缓存失败: userId={}, error={}", userId, e.getMessage(), e);
    }
  }
}
