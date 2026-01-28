package wake.su.zhuque.admin.security;

import java.util.Set;

import wake.su.zhuque.common.config.SuperAdminConfig;
import wake.su.zhuque.common.core.exception.BusinessException;

import lombok.extern.slf4j.Slf4j;

/**
 * 超级管理员工具类
 *
 * <p>用于判断当前用户是否为超级管理员，以及超级管理员的权限判断。</p>
 *
 * <p><strong>超级管理员特性：</strong></p> <ul> <li>禁止删除（用户、角色、权限数据受保护）</li> <li>自动拥有所有权限</li> <li>绕过所有权限检查</li> </ul>
 *
 * @author 朱雀项目
 * @since 2.0.0
 */
@Slf4j
public class SuperAdminHolder {

  /**
   * 判断是否为超级管理员
   *
   * @param userId
   *          用户ID
   * @param config
   *          超级管理员配置
   * @return true 如果是超级管理员
   */
  public static boolean isSuperAdmin(Long userId, SuperAdminConfig config) {
    if(userId == null || config == null) {
      return false;
    }
    return config.isSuperAdmin(userId);
  }

  /**
   * 超级管理员拥有所有权限
   *
   * <p>此方法用于权限校验逻辑中，如果判断是超级管理员，直接返回 true。</p>
   *
   * @param userId
   *          用户ID
   * @param config
   *          超级管理员配置
   * @param requiredPermission
   *          所需权限（不使用，超级管理员自动拥有所有权限）
   * @return true 如果是超级管理员
   */
  public static boolean hasPermission(Long userId, SuperAdminConfig config, String requiredPermission) {
    return isSuperAdmin(userId, config);
  }

  /**
   * 超级管理员拥有所有权限（批量权限检查）
   *
   * @param userId
   *          用户ID
   * @param config
   *          超级管理员配置
   * @param requiredPermissions
   *          所需权限列表
   * @param logical
   *          true=AND（需要所有权限）, false=OR（满足任意一个即可）
   * @return true 如果是超级管理员
   */
  public static boolean hasPermissions(Long userId, SuperAdminConfig config, Set<String> requiredPermissions,
      boolean logical) {
    // 超级管理员自动拥有所有权限
    return isSuperAdmin(userId, config);
  }

  /**
   * 记录超级管理员操作日志
   *
   * @param userId
   *          用户ID
   * @param operation
   *          操作描述
   * @param config
   *          超级管理员配置
   */
  public static void logOperation(Long userId, String operation, SuperAdminConfig config) {
    if(isSuperAdmin(userId, config)) {
      log.info("[超级管理员] 用户ID: {} 执行操作: {}", userId, operation);
    }
  }

  /**
   * 检查是否可以删除（禁止删除超级管理员）
   *
   * @param userId
   *          用户ID
   * @param config
   *          超级管理员配置
   * @throws BusinessException
   *           如果是超级管理员
   */
  public static void checkNotSuperAdmin(Long userId, SuperAdminConfig config) {
    if(isSuperAdmin(userId, config)) {
      throw new BusinessException("超级管理员账号受保护，禁止删除或禁用");
    }
  }

  /**
   * 检查是否可以删除（批量，禁止删除包含超级管理员）
   *
   * @param userIds
   *          用户ID列表
   * @param config
   *          超级管理员配置
   * @throws BusinessException
   *           如果列表中包含超级管理员
   */
  public static void checkNotSuperAdmin(java.util.List<Long> userIds, SuperAdminConfig config) {
    if(config.isAnySuperAdmin(userIds)) {
      throw new BusinessException("超级管理员账号受保护，禁止删除或禁用");
    }
  }
}
