package wake.su.zhuque.common.security.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import wake.su.zhuque.common.core.exception.BusinessException;
import wake.su.zhuque.common.security.annotation.RequiresPermission;
import wake.su.zhuque.common.util.JwtUtil;

import java.util.Arrays;
import java.util.List;

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

    // TODO: 注入PermissionService（需要解决循环依赖问题）
    // private final PermissionService permissionService;

    /**
     * 拦截@RequiresPermission注解
     */
    @Before("@annotation(requiresPermission)")
    public void checkPermission(JoinPoint joinPoint, RequiresPermission requiresPermission) {
        // 获取当前用户ID
        Long userId = getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("未登录或登录已过期");
        }

        String[] permissionCodes = requiresPermission.value();
        if (permissionCodes.length == 0) {
            // 没有指定权限，默认通过
            return;
        }

        List<String> permissionList = Arrays.asList(permissionCodes);
        RequiresPermission.LogicalType logicalType = requiresPermission.logical();

        // TODO: 实现权限校验逻辑
        // 暂时跳过权限校验，等Redis缓存实现后再完善
        log.debug("用户{}请求权限校验：{}，逻辑类型：{}", userId, permissionList, logicalType);

        /*
        boolean hasPermission = permissionService.hasPermissions(userId, permissionList,
            logicalType == RequiresPermission.LogicalType.AND);

        if (!hasPermission) {
            throw new BusinessException("权限不足，需要权限：" + String.join(",", permissionList));
        }
        */
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return null;
            }

            HttpServletRequest request = attributes.getRequest();
            String token = request.getHeader("Authorization");

            if (token == null || !token.startsWith("Bearer ")) {
                return null;
            }

            token = token.substring(7);
            return jwtUtil.getUserId(token);
        } catch (Exception e) {
            log.error("获取当前用户ID失败", e);
            return null;
        }
    }
}
