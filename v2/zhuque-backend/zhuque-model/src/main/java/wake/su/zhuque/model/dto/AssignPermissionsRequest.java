package wake.su.zhuque.model.dto;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 分配权限请求DTO
 *
 * @author wake.su
 * @since 2026-01-09
 */
@Data
public class AssignPermissionsRequest {

    /**
     * 角色ID
     * 从路径变量中获取，不需要客户端传入
     */
    private Long roleId;

    /**
     * 权限ID列表
     */
    @NotEmpty(message = "权限ID列表不能为空")
    private List<Long> permissionIds;
}
