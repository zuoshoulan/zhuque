package wake.su.zhuque.service;

import wake.su.zhuque.model.dto.LoginRequest;
import wake.su.zhuque.model.dto.LoginResponse;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest loginRequest);

    /**
     * 用户登出
     *
     * @param token 令牌
     */
    void logout(String token);
}
