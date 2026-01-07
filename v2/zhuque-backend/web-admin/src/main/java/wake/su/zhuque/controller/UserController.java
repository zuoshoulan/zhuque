package wake.su.zhuque.controller;

import wake.su.zhuque.common.core.result.Result;
import wake.su.zhuque.model.entity.SysUser;
import wake.su.zhuque.service.api.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 根据用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/{userId}")
    public Result<SysUser> getUserById(@PathVariable("userId") Long userId) {
        SysUser user = sysUserService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 清除密码字段，不返回给前端
        user.setPassword(null);
        return Result.success(user);
    }
}
