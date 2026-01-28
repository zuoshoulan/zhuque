package wake.su.zhuque.admin.security.aspect;

import java.util.Arrays;
import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import wake.su.zhuque.common.core.exception.BusinessException;
import wake.su.zhuque.common.security.annotation.RequiresPermission;
import wake.su.zhuque.common.security.validator.PermissionValidator;
import wake.su.zhuque.common.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 权限校验切面
 *
 * @author wake.su
 * @since 2026-01-09
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

  private final JwtUtil jwtUtil;

  /**
   * 使用@Lazy延迟注入，避免循环依赖 PermissionValidator接口在common模块，实现在service模块 通过@Lazy打破循环依赖，使用构造函数注入
   */
  @Lazy
  private final PermissionValidator permissionValidator;

  /**
   * 拦截@RequiresPermission注解
   */
  @Before("@annotation(requiresPermission)")
  public void checkPermission(JoinPoint joinPoint, RequiresPermission requiresPermission) {
    // 获取当前用户ID
    Long userId = getCurrentUserId();
    if(userId == null) {
      throw new BusinessException("未登录或登录已过期");
    }

    String[] permissionCodes = requiresPermission.value();
    if(permissionCodes.length == 0) {
      // 没有指定权限，默认通过
      return;
    }

    List<String> permissionList = Arrays.asList(permissionCodes);
    RequiresPermission.LogicalType logicalType = requiresPermission.logical();

    // 检查是否安装了PermissionValidator
    if(permissionValidator == null) {
      log.warn("PermissionValidator未注入，跳过权限校验: userId={}, permissions={}", userId, permissionList);
      // 在开发环境可以跳过，生产环境应该强制校验
      return;
    }

    // 执行权限校验
    boolean hasPermission = permissionValidator.hasPermissions(userId, permissionList,
        logicalType == RequiresPermission.LogicalType.AND);

    if(!hasPermission) {
      log.warn("权限不足: userId={}, requiredPermissions={}, logicalType={}", userId, permissionList, logicalType);
      throw new BusinessException("权限不足，需要权限：" + String.join(" 或 ", permissionList));
    }

    log.debug("权限校验通过: userId={}, permissions={}", userId, permissionList);
  }

  /**
   * 获取当前登录用户ID
   */
  private Long getCurrentUserId() {
    try {
      ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
      if(attributes == null) {
        return null;
      }

      HttpServletRequest request = attributes.getRequest();
      String token = request.getHeader("Authorization");

      if(token == null || !token.startsWith("Bearer ")) {
        return null;
      }

      token = token.substring(7);
      return jwtUtil.getUserId(token);
    } catch(Exception e) {
      log.error("获取当前用户ID失败", e);
      return null;
    }
  }
}
