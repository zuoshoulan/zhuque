package wake.su.zhuque.model.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.*;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user_permission")
public class SysUserPermissionDO {

  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  /**
   * 用户ID
   */
  private Long userId;

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
