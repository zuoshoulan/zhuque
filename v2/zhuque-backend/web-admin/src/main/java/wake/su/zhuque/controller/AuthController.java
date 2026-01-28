package wake.su.zhuque.controller;

import org.springframework.web.bind.annotation.*;

import wake.su.zhuque.common.core.result.Result;
import wake.su.zhuque.model.dto.LoginRequest;
import wake.su.zhuque.model.dto.LoginResponse;
import wake.su.zhuque.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 认证控制器
 */
@Tag(name = "认证授权", description = "用户登录、登出、Token管理")
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  /**
   * 用户登录
   *
   * @param loginRequest
   *          登录请求
   * @return 登录响应
   */
  @Operation(summary = "用户登录", description = "使用账号（手机号）和密码登录，成功后返回JWT Token。第一版仅支持手机号登录。")
  @ApiResponse(responseCode = "200", description = "登录成功")
  @ApiResponse(responseCode = "401", description = "用户名或密码错误")
  @PostMapping("/login")
  public Result<LoginResponse> login(
      @Parameter(description = "登录请求", required = true) @Valid @RequestBody LoginRequest loginRequest) {
    log.info("用户登录请求: account={}", loginRequest.getAccount());
    try {
      LoginResponse response = authService.login(loginRequest);
      return Result.success(response);
    } catch(Exception e) {
      log.error("登录失败: {}", e.getMessage());
      return Result.error(e.getMessage());
    }
  }

  /**
   * 用户登出
   *
   * @param token
   *          令牌
   * @return 成功响应
   */
  @Operation(summary = "用户登出", description = "用户退出登录，Token失效")
  @ApiResponse(responseCode = "200", description = "登出成功")
  @PostMapping("/logout")
  public Result<Void> logout(
      @Parameter(description = "JWT Token", example = "Bearer eyJhbGciOiJIUzI1NiJ9...") @RequestHeader("Authorization") String token) {
    log.info("用户登出");
    authService.logout(token);
    return Result.success();
  }
}
