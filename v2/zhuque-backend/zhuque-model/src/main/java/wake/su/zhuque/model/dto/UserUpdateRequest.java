package wake.su.zhuque.model.dto;

import lombok.Data;

/**
 * 用户更新请求
 */
@Data
public class UserUpdateRequest {
    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 密码（创建用户时必填）
     */
    private String password;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;
}
