package wake.su.zhuque.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wake.su.zhuque.common.core.result.Result;
import wake.su.zhuque.model.dto.LoginRequest;
import wake.su.zhuque.model.dto.LoginResponse;
import wake.su.zhuque.service.AuthService;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private AuthService authService;

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("用户登录请求: account={}", loginRequest.getAccount());
        try {
            LoginResponse response = authService.login(loginRequest);
            return Result.success(response);
        } catch (Exception e) {
            log.error("登录失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户登出
     *
     * @param token 令牌
     * @return 成功响应
     */
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String token) {
        log.info("用户登出");
        authService.logout(token);
        return Result.success();
    }
}
