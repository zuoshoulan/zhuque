package wake.su.zhuque.model.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.*;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_role_permission")
public class SysRolePermissionDO {

  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  /**
   * 角色ID
   */
  private Long roleId;

  /**
   * 权限ID
   */
  private Long permissionId;

  /**
   * 创建时间
   */
  @TableField("create_time")
  private LocalDateTime createTime;

  /**
   * 创建人
   */
  @TableField("create_by")
  private String createBy;
}
