package wake.su.zhuque.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wake.su.zhuque.common.util.JwtUtil;
import wake.su.zhuque.model.dto.LoginRequest;
import wake.su.zhuque.model.dto.LoginResponse;
import wake.su.zhuque.model.entity.SysUserDO;
import wake.su.zhuque.service.AuthService;
import wake.su.zhuque.service.SysUserService;

/**
 * 认证服务实现
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        log.info("用户登录: username={}", username);

        // 查询用户
        SysUserDO user = sysUserService.getOne(
            new LambdaQueryWrapper<SysUserDO>()
                .eq(SysUserDO::getUsername, username)
        );

        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 验证密码
        if (!PasswordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        // 生成Token
        String token = jwtUtil.generateToken(user.getId().toString());

        // 构建响应
        LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .forceChangePassword(user.getForceChangePassword() == 1)
                .build();

        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .userInfo(userInfo)
                .build();
    }

    @Override
    public void logout(String token) {
        // TODO: 实现登出逻辑，可以将token加入黑名单（使用Redis）
        log.info("用户登出");
    }

    /**
     * 密码编码器（内部类）
     */
    private static class PasswordEncoder {
        /**
         * 匹配密码
         *
         * @param rawPassword     原始密码
         * @param encodedPassword 加密后的密码
         * @return 是否匹配
         */
        public static boolean matches(String rawPassword, String encodedPassword) {
            // TODO: 实现BCrypt密码验证
            // 暂时使用简单比较，后续需要替换为BCrypt
            return rawPassword.equals(encodedPassword);
        }
    }
}
