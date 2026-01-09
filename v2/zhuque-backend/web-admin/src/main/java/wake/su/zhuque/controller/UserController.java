package wake.su.zhuque.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wake.su.zhuque.common.core.result.Result;
import wake.su.zhuque.model.dto.PageResult;
import wake.su.zhuque.model.dto.UserQueryRequest;
import wake.su.zhuque.model.dto.UserUpdateRequest;
import wake.su.zhuque.model.entity.SysUserDO;
import wake.su.zhuque.service.SysUserService;

/**
 * 用户管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private SysUserService sysUserService;

    /**
     * 根据用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户信息
     */
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
    @PutMapping("/{userId}/status")
    public Result<Void> updateStatus(@PathVariable("userId") Long userId, @RequestBody UserUpdateRequest request) {
        boolean success = sysUserService.updateStatus(userId, request.getStatus());
        return success ? Result.success() : Result.error("更新失败");
    }
}
