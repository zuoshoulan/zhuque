package wake.su.zhuque.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限VO
 *
 * @author wake.su
 * @since 2026-01-09
 */
@Data
public class PermissionVO {

    /**
     * 权限ID
     */
    private Long id;

    /**
     * 父权限ID
     */
    private Long parentId;

    /**
     * 权限编码
     */
    private String permissionCode;

    /**
     * 权限名称
     */
    private String permissionName;

    /**
     * 权限类型：1-路由 2-按钮 3-接口
     */
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

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 子权限列表
     */
    private List<PermissionVO> children;
}
