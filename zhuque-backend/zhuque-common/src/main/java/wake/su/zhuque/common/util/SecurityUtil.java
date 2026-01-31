package wake.su.zhuque.common.util;

import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全工具类 用于获取当前登录用户信息
 */
public class SecurityUtil {

  /**
   * 获取当前登录用户的ID
   *
   * @return 用户ID
   */
  public static Long getCurrentUserId() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principal instanceof Long) {
      return (Long) principal;
    }
    return null;
  }

  /**
   * 获取当前登录用户的用户名
   *
   * @return 用户名
   */
  public static String getCurrentUsername() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principal instanceof String) {
      return (String) principal;
    }
    return null;
  }

  /**
   * 判断是否已认证
   *
   * @return true=已认证，false=未认证
   */
  public static boolean isAuthenticated() {
    return SecurityContextHolder.getContext().getAuthentication() != null
        && SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
  }
}
