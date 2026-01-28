package wake.su.zhuque.model.vo;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

/**
 * 菜单VO
 *
 * @author wake.su
 * @since 2026-01-09
 */
@Data
public class MenuVO {

  /**
   * 菜单ID
   */
  private Long id;

  /**
   * 父菜单ID
   */
  private Long parentId;

  /**
   * 菜单名称
   */
  private String menuName;

  /**
   * 菜单类型：1-目录 2-菜单 3-按钮
   */
  private Integer menuType;

  /**
   * 菜单图标
   */
  private String icon;

  /**
   * 路由路径
   */
  private String path;

  /**
   * 组件路径
   */
  private String component;

  /**
   * 权限编码
   */
  private String permissionCode;

  /**
   * 排序号
   */
  private Integer sortOrder;

  /**
   * 是否显示：0-隐藏 1-显示
   */
  private Integer visible;

  /**
   * 状态：0-禁用 1-启用
   */
  private Integer status;

  /**
   * 创建时间
   */
  private LocalDateTime createTime;

  /**
   * 子菜单列表
   */
  private List<MenuVO> children;
}
