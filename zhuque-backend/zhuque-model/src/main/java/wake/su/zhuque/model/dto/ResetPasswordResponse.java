package wake.su.zhuque.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 重置密码响应DTO
 *
 * @author wake.su
 * @since 2026-01-10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "重置密码响应")
public class ResetPasswordResponse {

  /**
   * 用户ID
   */
  @Schema(description = "用户ID", example = "1")
  private Long userId;

  /**
   * 用户名
   */
  @Schema(description = "用户名", example = "admin")
  private String username;

  /**
   * 手机号
   */
  @Schema(description = "手机号", example = "13800138001")
  private String phone;

  /**
   * 新密码（明文，仅此一次返回）
   */
  @Schema(description = "新密码（明文，请妥善保管，仅此一次返回）", example = "2026011013800138001")
  private String password;

  /**
   * 密码类型
   */
  @Schema(description = "密码类型：custom-自定义，default-默认规则", example = "default")
  private String passwordType;
}
