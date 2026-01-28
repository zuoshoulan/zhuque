package wake.su.zhuque.model.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.*;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_role")
public class SysRoleDO {

  @TableId(value = "id", type = IdType.AUTO)
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
   * 描述
   */
  private String description;

  /**
   * 状态：0-禁用 1-启用
   */
  private Integer status;

  /**
   * 排序
   */
  private Integer sortOrder;

  /**
   * 创建时间
   */
  @TableField("create_time")
  private LocalDateTime createTime;

  /**
   * 更新时间
   */
  @TableField("update_time")
  private LocalDateTime updateTime;

  /**
   * 创建人
   */
  @TableField("create_by")
  private String createBy;

  /**
   * 更新人
   */
  @TableField("update_by")
  private String updateBy;

  /**
   * 删除标记：0-未删除 1-已删除
   */
  @TableLogic
  private Integer deleted;
}
