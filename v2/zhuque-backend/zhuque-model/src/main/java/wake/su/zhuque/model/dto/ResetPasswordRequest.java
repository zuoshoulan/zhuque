package wake.su.zhuque.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 重置密码请求DTO
 *
 * @author wake.su
 * @since 2026-01-10
 */
@Data
@Schema(description = "重置密码请求")
public class ResetPasswordRequest {

  /**
   * 用户ID
   */
  @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
  private Long userId;

  /**
   * 新密码（可选，不传则使用默认规则生成）
   */
  @Schema(description = "新密码，不传则使用默认规则（yyyyMMdd+手机号）生成", example = "2026011013800138001")
  private String newPassword;
}
