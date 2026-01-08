package wake.su.zhuque.controller;

import jakarta.annotation.Resource;
import wake.su.zhuque.common.core.result.Result;
import wake.su.zhuque.model.entity.SysUserDO;
import wake.su.zhuque.service.SysUserService;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
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
}
