package wake.su.zhuque.service.impl;

import org.springframework.stereotype.Service;

import wake.su.zhuque.common.config.SuperAdminConfig;
import wake.su.zhuque.common.security.util.PasswordUtil;
import wake.su.zhuque.common.util.JwtUtil;
import wake.su.zhuque.model.dto.LoginRequest;
import wake.su.zhuque.model.dto.LoginResponse;
import wake.su.zhuque.model.entity.SysUserDO;
import wake.su.zhuque.service.AuthService;
import wake.su.zhuque.service.SysUserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 认证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final SysUserService sysUserService;
  private final JwtUtil jwtUtil;
  private final SuperAdminConfig superAdminConfig;

  @Override
  public LoginResponse login(LoginRequest loginRequest) {
    String account = loginRequest.getAccount();
    String password = loginRequest.getPassword();

    log.info("用户登录: account={}", account);

    // 查询用户
    SysUserDO user = sysUserService.getByAccount(account);

    if (user == null) {
      throw new RuntimeException("账号或密码错误");
    }

    // 验证密码
    if (!PasswordUtil.matches(password, user.getPassword())) {
      throw new RuntimeException("用户名或密码错误");
    }

    // 检查用户状态
    if (user.getStatus() == 0) {
      throw new RuntimeException("账号已被禁用");
    }

    // 生成Token，将username存入claims
    java.util.Map<String, Object> claims = new java.util.HashMap<>();
    claims.put("username", user.getUsername());
    String token = jwtUtil.generateToken(user.getId().toString(), claims);

    // 构建响应
    LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder().id(user.getId()).username(user.getUsername())
        .nickname(user.getNickname()).email(user.getEmail()).phone(user.getPhone()).avatar(user.getAvatar())
        .forceChangePassword(user.getForceChangePassword() == 1)
        .isSuperAdmin(superAdminConfig.isSuperAdmin(user.getId())).build();

    return LoginResponse.builder().accessToken(token).tokenType("Bearer").userInfo(userInfo).build();
  }

  @Override
  public void logout(String token) {
    // TODO: 实现登出逻辑，可以将token加入黑名单（使用Redis）
    log.info("用户登出");
  }
}
