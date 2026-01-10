package wake.su.zhuque.service.impl.permission;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wake.su.zhuque.common.security.validator.PermissionValidator;
import wake.su.zhuque.service.api.PermissionService;

import java.util.List;

/**
 * 权限验证器实现
 * 实现权限验证逻辑，避免循环依赖
 *
 * @author wake.su
 * @since 2026-01-10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionValidatorImpl implements PermissionValidator {

    private final PermissionService permissionService;

    @Override
    public boolean hasPermissions(Long userId, List<String> permissionCodes, boolean requireAll) {
        if (userId == null || permissionCodes == null || permissionCodes.isEmpty()) {
            return false;
        }

        log.debug("验证用户权限: userId={}, permissions={}, requireAll={}",
                  userId, permissionCodes, requireAll);

        return permissionService.hasPermissions(userId, permissionCodes, requireAll);
    }
}
