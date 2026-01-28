package wake.su.zhuque.common.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * 超级管理员配置
 *
 * <p>通过配置文件指定超级管理员的用户ID列表，提供以下保护：</p> <ul> <li>禁止删除超级管理员对应的用户数据</li> <li>禁止删除超级管理员拥有的角色和权限</li>
 * <li>超级管理员自动拥有所有权限（绕过权限校验）</li> </ul>
 *
 * <p><strong>重要提示：</strong></p> <ul> <li>超级管理员仍然是数据库中的普通用户，正常登录即可</li> <li>配置文件只是指定哪些用户ID需要特殊保护</li>
 * <li>即使数据库权限数据被误删，超级管理员仍然可以访问所有功能</li> </ul>
 *
 * @author 朱雀项目
 * @since 2.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "super-admin")
public class SuperAdminConfig {

  /**
   * 是否启用超级管理员保护
   *
   * <p>默认启用。如果设置为 false，则不进行任何保护检查。</p>
   */
  private boolean enabled = true;

  /**
   * 超级管理员的用户ID列表
   *
   * <p>这些用户ID对应的用户数据将被保护：</p> <ul> <li>禁止删除用户</li> <li>禁止禁用用户</li> <li>自动拥有所有权限</li> </ul>
   *
   * <p><strong>配置示例：</strong></p> <pre> super-admin: enabled: true user-ids: - 1 # admin用户 - 2 # 系统维护账号 </pre>
   */
  private List<Long> userIds = new ArrayList<>();

  /**
   * 检查是否为超级管理员
   *
   * @param userId
   *          用户ID
   * @return true 如果是超级管理员
   */
  public boolean isSuperAdmin(Long userId) {
    if(!enabled || userId == null) {
      return false;
    }
    return userIds.contains(userId);
  }

  /**
   * 检查是否为超级管理员（多个ID）
   *
   * @param userIds
   *          用户ID列表
   * @return true 如果任意一个ID是超级管理员
   */
  public boolean isAnySuperAdmin(List<Long> userIds) {
    if(!enabled || userIds == null || userIds.isEmpty()) {
      return false;
    }
    return userIds.stream().anyMatch(this.userIds::contains);
  }

  /**
   * 获取超级管理员ID列表（用于日志）
   *
   * @return ID列表字符串，如 "[1, 2]"
   */
  public String getAdminIdsString() {
    return userIds.toString();
  }
}
