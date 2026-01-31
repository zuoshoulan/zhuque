package wake.su.zhuque.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建权限请求DTO
 *
 * @author wake.su
 * @since 2026-01-09
 */
@Data
@Schema(description = "权限创建请求")
public class PermissionCreateRequest {

  /**
   * 父权限ID
   */
  @Schema(description = "父权限ID，0表示根权限", example = "0")
  private Long parentId;

  /**
   * 权限编码
   */
  @Schema(description = "权限编码，唯一标识", example = "advertiser:create")
  @NotBlank(message = "权限编码不能为空")
  private String permissionCode;

  /**
   * 权限名称
   */
  @Schema(description = "权限名称", example = "创建广告主")
  @NotBlank(message = "权限名称不能为空")
  private String permissionName;

  /**
   * 权限类型：1-路由 2-按钮 3-接口
   */
  @Schema(description = "权限类型：1-路由 2-按钮 3-接口", example = "2")
  @NotNull(message = "权限类型不能为空")
  private Integer permissionType;

  /**
   * 路由路径/接口路径
   */
  @Schema(description = "路由路径或接口路径", example = "/api/advertisers")
  private String path;

  /**
   * HTTP方法
   */
  @Schema(description = "HTTP方法：GET/POST/PUT/DELETE", example = "POST")
  private String method;

  /**
   * 图标
   */
  @Schema(description = "菜单图标", example = "Plus")
  private String icon;

  /**
   * 排序号
   */
  @Schema(description = "排序号，数字越小越靠前", example = "1")
  private Integer sortOrder;

  /**
   * 状态：0-禁用 1-启用
   */
  @Schema(description = "状态：0-禁用，1-启用", example = "1")
  private Integer status;
}
