package wake.su.zhuque.model.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 创建角色请求DTO
 *
 * @author wake.su
 * @since 2026-01-09
 */
@Data
public class RoleCreateRequest {

    /**
     * 角色编码
     */
    @NotBlank(message = "角色编码不能为空")
    private String roleCode;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 权限ID列表
     */
    private List<Long> permissionIds;
}
