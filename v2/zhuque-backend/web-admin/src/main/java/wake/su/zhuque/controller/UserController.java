package wake.su.zhuque.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wake.su.zhuque.common.core.result.Result;
import wake.su.zhuque.model.dto.PageResult;
import wake.su.zhuque.model.dto.ResetPasswordRequest;
import wake.su.zhuque.model.dto.ResetPasswordResponse;
import wake.su.zhuque.model.dto.UserQueryRequest;
import wake.su.zhuque.model.dto.UserUpdateRequest;
import wake.su.zhuque.model.entity.SysUserDO;
import wake.su.zhuque.model.vo.RoleVO;
import wake.su.zhuque.service.SysUserService;

import java.util.List;
import java.util.Map;

/**
 * 用户管理控制器
 */
@Tag(name = "用户管理", description = "用户CRUD、密码重置接口")
@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final SysUserService sysUserService;

    /**
     * 根据用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @Operation(summary = "获取用户详情", description = "根据ID查询用户信息")
    @GetMapping("/{userId}")
    public Result<SysUserDO> getUserById(@PathVariable("userId") Long userId) {
        SysUserDO user = sysUserService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 清除密码字段，不返回给前端
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 分页查询用户
     *
     * @param request 查询请求
     * @return 分页结果
     */
    @Operation(summary = "分页查询用户", description = "支持关键词搜索、状态筛选")
    @GetMapping("/page")
    public Result<PageResult<SysUserDO>> page(UserQueryRequest request) {
        PageResult<SysUserDO> pageResult = sysUserService.page(request);
        // 清除密码字段
        if (pageResult.getRecords() != null) {
            pageResult.getRecords().forEach(user -> user.setPassword(null));
        }
        return Result.success(pageResult);
    }

    /**
     * 创建用户
     *
     * @param request 创建请求
     * @return 用户ID
     */
    @Operation(summary = "创建用户", description = "创建新用户，密码使用默认规则生成")
    @PostMapping
    public Result<Long> createUser(@RequestBody UserUpdateRequest request) {
        Long userId = sysUserService.createUser(request);
        return Result.success(userId);
    }

    /**
     * 更新用户
     *
     * @param userId  用户ID
     * @param request 更新请求
     * @return 是否成功
     */
    @Operation(summary = "更新用户", description = "更新用户基本信息")
    @PutMapping("/{userId}")
    public Result<Void> updateUser(@PathVariable("userId") Long userId, @RequestBody UserUpdateRequest request) {
        boolean success = sysUserService.updateUser(userId, request);
        return success ? Result.success() : Result.error("更新失败");
    }

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    @Operation(summary = "删除用户", description = "删除指定用户")
    @DeleteMapping("/{userId}")
    public Result<Void> deleteUser(@PathVariable("userId") Long userId) {
        boolean success = sysUserService.removeById(userId);
        return success ? Result.success() : Result.error("删除失败");
    }

    /**
     * 更新用户状态
     *
     * @param userId 用户ID
     * @param request 更新请求
     * @return 是否成功
     */
    @Operation(summary = "更新用户状态", description = "启用或禁用用户")
    @PutMapping("/{userId}/status")
    public Result<Void> updateStatus(@PathVariable("userId") Long userId, @RequestBody UserUpdateRequest request) {
        boolean success = sysUserService.updateStatus(userId, request.getStatus());
        return success ? Result.success() : Result.error("更新失败");
    }

    /**
     * 更新用户主题偏好
     *
     * @param userId 用户ID
     * @param theme  主题偏好 (light/dark/auto)
     * @return 是否成功
     */
    @Operation(summary = "更新用户主题偏好", description = "更新用户主题偏好：light-亮色，dark-暗色，auto-自动")
    @PutMapping("/{userId}/theme")
    public Result<Void> updateThemePreference(
            @PathVariable("userId") Long userId,
            @RequestBody java.util.Map<String, String> request) {
        String theme = request.get("theme");
        boolean success = sysUserService.updateThemePreference(userId, theme);
        return success ? Result.success() : Result.error("更新失败");
    }

    /**
     * 重置用户密码
     *
     * @param userId  用户ID
     * @param request 重置密码请求
     * @return 重置密码响应（包含明文密码，仅此一次返回）
     */
    @Operation(
        summary = "重置用户密码",
        description = "重置用户密码，可选择自定义密码或使用默认规则（yyyyMMdd+手机号）。成功后返回明文密码，请妥善保管。"
    )
    @PostMapping("/{userId}/reset-password")
    public Result<ResetPasswordResponse> resetPassword(
            @Parameter(description = "用户ID", required = true, example = "3")
            @PathVariable Long userId,
            @Valid @RequestBody ResetPasswordRequest request) {
        log.info("重置用户密码: userId={}", userId);
        try {
            ResetPasswordResponse response = sysUserService.resetPassword(userId, request.getNewPassword());
            return Result.success(response);
        } catch (Exception e) {
            log.error("重置密码失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取用户的角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Operation(summary = "获取用户的角色列表", description = "获取指定用户拥有的所有角色")
    @GetMapping("/{userId}/roles")
    public Result<List<RoleVO>> getUserRoles(@PathVariable("userId") Long userId) {
        List<RoleVO> roles = sysUserService.getUserRoles(userId);
        return Result.success(roles);
    }

    /**
     * 为用户分配角色
     *
     * @param userId 用户ID
     * @param request 包含角色ID列表的请求
     * @return 是否成功
     */
    @Operation(summary = "为用户分配角色", description = "为用户分配多个角色，会覆盖用户原有的所有角色")
    @PostMapping("/{userId}/roles")
    public Result<Void> assignRoles(
            @PathVariable("userId") Long userId,
            @RequestBody Map<String, List<Long>> request) {
        List<Long> roleIds = request.get("roleIds");
        boolean success = sysUserService.assignRoles(userId, roleIds);
        return success ? Result.success() : Result.error("分配角色失败");
    }

    /**
     * 修改密码
     *
     * @param userId  用户ID
     * @param request 修改密码请求
     * @return 是否成功
     */
    @Operation(
        summary = "修改密码",
        description = "用户自己修改密码，需要输入原密码验证"
    )
    @PostMapping("/{userId}/change-password")
    public Result<Void> changePassword(
            @Parameter(description = "用户ID", required = true, example = "3")
            @PathVariable Long userId,
            @Valid @RequestBody wake.su.zhuque.model.dto.ChangePasswordRequest request) {
        log.info("修改密码: userId={}", userId);
        try {
            sysUserService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
            return Result.success();
        } catch (Exception e) {
            log.error("修改密码失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
}
