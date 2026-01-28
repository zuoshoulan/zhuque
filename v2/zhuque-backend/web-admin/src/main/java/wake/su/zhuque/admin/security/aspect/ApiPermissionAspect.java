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
import wake.su.zhuque.common.security.annotation.RequiresApiPermission;
import wake.su.zhuque.common.security.validator.PermissionValidator;
import wake.su.zhuque.common.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * API接口权限校验切面 第三层权限控制：接口级别的权限校验
 *
 * @author wake.su
 * @since 2026-01-11
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ApiPermissionAspect {

  private final JwtUtil jwtUtil;

  /**
   * 使用@Lazy延迟注入，避免循环依赖 PermissionValidator接口在common模块，实现在service模块
   */
  @Lazy
  private final PermissionValidator permissionValidator;

  /**
   * 拦截@RequiresApiPermission注解
   */
  @Before("@annotation(requiresApiPermission)")
  public void checkApiPermission(JoinPoint joinPoint, RequiresApiPermission requiresApiPermission) {
    // 获取当前用户ID
    Long userId = getCurrentUserId();
    if(userId == null) {
      throw new BusinessException("未登录或登录已过期");
    }

    String[] permissionCodes = requiresApiPermission.value();
    if(permissionCodes.length == 0) {
      // 没有指定权限，默认通过
      return;
    }

    List<String> permissionList = Arrays.asList(permissionCodes);
    RequiresApiPermission.LogicalType logicalType = requiresApiPermission.logical();

    // 检查是否安装了PermissionValidator
    if(permissionValidator == null) {
      log.warn("PermissionValidator未注入，跳过API权限校验: userId={}, apiPermissions={}", userId, permissionList);
      return;
    }

    // 执行API权限校验
    boolean hasPermission = permissionValidator.hasPermissions(userId, permissionList,
        logicalType == RequiresApiPermission.LogicalType.AND);

    if(!hasPermission) {
      log.warn("API权限不足: userId={}, requiredApiPermissions={}, logicalType={}", userId, permissionList, logicalType);
      throw new BusinessException("API权限不足，需要权限：" + String.join(" 或 ", permissionList));
    }

    log.debug("API权限校验通过: userId={}, apiPermissions={}", userId, permissionList);
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
