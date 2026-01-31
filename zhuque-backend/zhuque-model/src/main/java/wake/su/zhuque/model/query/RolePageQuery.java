package wake.su.zhuque.model.query;

import lombok.Data;

/**
 * 角色分页查询参数
 *
 * @author wake.su
 * @since 2026-01-10
 */
@Data
public class RolePageQuery {

  /**
   * 当前页
   */
  private Long current;

  /**
   * 每页大小
   */
  private Long size;

  /**
   * 搜索关键词（角色名称、角色编码）
   */
  private String keyword;

  /**
   * 状态：0-禁用，1-启用
   */
  private Integer status;
}
