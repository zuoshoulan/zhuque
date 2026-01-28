package wake.su.zhuque.model.vo;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

/**
 * 角色VO
 *
 * @author wake.su
 * @since 2026-01-09
 */
@Data
public class RoleVO {

  /**
   * 角色ID
   */
  private Long id;

  /**
   * 角色编码
   */
  private String roleCode;

  /**
   * 角色名称
   */
  private String roleName;

  /**
   * 角色描述
   */
  private String description;

  /**
   * 状态：0-禁用 1-启用
   */
  private Integer status;

  /**
   * 创建时间
   */
  private LocalDateTime createTime;

  /**
   * 更新时间
   */
  private LocalDateTime updateTime;

  /**
   * 权限ID列表
   */
  private List<Long> permissionIds;

  /**
   * 权限列表
   */
  private List<PermissionVO> permissions;
}
