package wake.su.zhuque.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 登录请求DTO
 * <p>
 * 第一版：account 仅支持手机号（1[3-9]\d{9}）
 * 后续版本：可能扩展支持邮箱、用户名等
 * </p>
 */
@Data
public class LoginRequest {

    /**
     * 账号
     * <p>第一版：仅支持手机号</p>
     */
    @NotBlank(message = "账号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "账号格式不正确（第一版仅支持手机号）")
    private String account;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}
