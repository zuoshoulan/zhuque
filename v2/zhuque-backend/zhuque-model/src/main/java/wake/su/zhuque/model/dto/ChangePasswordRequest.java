package wake.su.zhuque.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改密码请求DTO
 *
 * @author wake.su
 * @since 2026-01-11
 */
@Data
@Schema(description = "修改密码请求")
public class ChangePasswordRequest {

    /**
     * 原密码
     */
    @Schema(description = "原密码", required = true, example = "oldPassword123")
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;

    /**
     * 新密码
     */
    @Schema(description = "新密码（至少8个字符）", required = true, example = "newPassword123")
    @NotBlank(message = "新密码不能为空")
    @Size(min = 8, message = "新密码长度不能少于8个字符")
    private String newPassword;
}
