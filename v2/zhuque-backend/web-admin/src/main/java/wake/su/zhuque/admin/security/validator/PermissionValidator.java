package wake.su.zhuque.admin.security.validator;

import java.util.List;

/**
 * 权限验证器接口
 * 用于解耦PermissionAspect和PermissionService之间的循环依赖
 *
 * @author wake.su
 * @since 2026-01-10
 */
public interface PermissionValidator {

    /**
     * 验证用户是否拥有指定权限
     *
     * @param userId         用户ID
     * @param permissionCodes 权限编码列表
     * @param requireAll     是否需要拥有所有权限（true=AND，false=OR）
     * @return 是否拥有权限
     */
    boolean hasPermissions(Long userId, List<String> permissionCodes, boolean requireAll);
}
