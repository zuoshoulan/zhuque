package wake.su.zhuque.model.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 创建权限请求DTO
 *
 * @author wake.su
 * @since 2026-01-09
 */
@Data
public class PermissionCreateRequest {

    /**
     * 父权限ID
     */
    private Long parentId;

    /**
     * 权限编码
     */
    @NotBlank(message = "权限编码不能为空")
    private String permissionCode;

    /**
     * 权限名称
     */
    @NotBlank(message = "权限名称不能为空")
    private String permissionName;

    /**
     * 权限类型：1-路由 2-按钮 3-接口
     */
    @NotNull(message = "权限类型不能为空")
    private Integer permissionType;

    /**
     * 路由路径/接口路径
     */
    private String path;

    /**
     * HTTP方法
     */
    private String method;

    /**
     * 图标
     */
    private String icon;

    /**
     * 排序号
     */
    private Integer sortOrder;
}
