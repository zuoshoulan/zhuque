package wake.su.zhuque.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wake.su.zhuque.common.core.result.Result;
import wake.su.zhuque.service.impl.permission.PermissionCacheService;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 权限缓存管理控制器
 *
 * @author wake.su
 * @since 2026-01-10
 */
@Tag(name = "权限缓存管理", description = "用户权限缓存的刷新、清除等操作")
@Slf4j
@RestController
@RequestMapping("/api/cache/permissions")
@RequiredArgsConstructor
public class CacheController {

    private final PermissionCacheService permissionCacheService;

    /**
     * 刷新指定用户的权限缓存
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    @Operation(summary = "刷新用户权限缓存", description = "清除指定用户的权限缓存，下次查询时会重新从数据库加载")
    @DeleteMapping("/user/{userId}")
    public Result<Void> refreshUserPermissions(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId) {
        log.info("刷新用户权限缓存: userId={}", userId);
        permissionCacheService.clearUserPermissions(userId);
        return Result.success();
    }

    /**
     * 批量刷新用户权限缓存
     *
     * @param request 包含用户ID列表的请求
     * @return 是否成功
     */
    @Operation(summary = "批量刷新用户权限缓存", description = "批量清除多个用户的权限缓存")
    @DeleteMapping("/batch")
    public Result<Map<String, Object>> refreshUserPermissionsBatch(
            @RequestBody Map<String, List<Long>> request) {
        List<Long> userIds = request.get("userIds");
        if (userIds == null || userIds.isEmpty()) {
            return Result.error("用户ID列表不能为空");
        }

        log.info("批量刷新用户权限缓存: userIds={}", userIds);
        Set<Long> userIdSet = Set.copyOf(userIds);
        permissionCacheService.clearUserPermissionsBatch(userIdSet);

        return Result.success(Map.of(
            "count", userIdSet.size(),
            "message", "成功清除 " + userIdSet.size() + " 个用户的权限缓存"
        ));
    }

    /**
     * 清除所有权限缓存
     * 警告：此操作会影响所有用户的权限验证，仅在必要时使用
     *
     * @return 是否成功
     */
    @Operation(
        summary = "清除所有权限缓存",
        description = "清除所有用户的权限缓存。警告：此操作会影响所有用户的权限验证，仅在必要时使用！"
    )
    @DeleteMapping("/all")
    public Result<Void> clearAllPermissions() {
        log.warn("清除所有权限缓存 - 此操作会影响所有用户");
        permissionCacheService.clearAllPermissions();
        return Result.success();
    }

    /**
     * 检查用户权限缓存是否存在
     *
     * @param userId 用户ID
     * @return 是否存在缓存
     */
    @Operation(summary = "检查用户权限缓存", description = "检查指定用户的权限缓存是否存在")
    @GetMapping("/user/{userId}/exists")
    public Result<Map<String, Object>> checkUserPermissionCache(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId) {
        boolean exists = permissionCacheService.hasPermissionCache(userId);
        return Result.success(Map.of(
            "userId", userId,
            "hasCache", exists,
            "message", exists ? "权限缓存存在" : "权限缓存不存在"
        ));
    }

    /**
     * 刷新用户权限缓存过期时间
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    @Operation(summary = "刷新缓存过期时间", description = "延长用户权限缓存的过期时间")
    @PutMapping("/user/{userId}/refresh")
    public Result<Void> refreshPermissionCacheTTL(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId) {
        log.info("刷新权限缓存过期时间: userId={}", userId);
        permissionCacheService.refreshPermissionCache(userId);
        return Result.success();
    }
}
