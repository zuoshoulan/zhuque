package wake.su.zhuque.service.api;

import wake.su.zhuque.common.core.result.Result;
import wake.su.zhuque.model.dto.AssignPermissionsRequest;
import wake.su.zhuque.model.dto.RoleCreateRequest;
import wake.su.zhuque.model.query.RolePageQuery;
import wake.su.zhuque.model.vo.PermissionVO;
import wake.su.zhuque.model.vo.RoleVO;

import java.util.List;

/**
 * 角色管理服务接口
 *
 * @author wake.su
 * @since 2026-01-09
 */
public interface RoleService {

    /**
     * 创建角色
     *
     * @param request 创建请求
     * @return 角色ID
     */
    Long createRole(RoleCreateRequest request);

    /**
     * 更新角色
     *
     * @param id 角色ID
     * @param request 更新请求
     */
    void updateRole(Long id, RoleCreateRequest request);

    /**
     * 删除角色
     *
     * @param id 角色ID
     */
    void deleteRole(Long id);

    /**
     * 获取角色详情
     *
     * @param id 角色ID
     * @return 角色VO
     */
    RoleVO getRole(Long id);

    /**
     * 获取角色列表
     *
     * @return 角色列表
     */
    List<RoleVO> getRoleList();

    /**
     * 分页查询角色列表
     *
     * @param query 查询参数
     * @return 分页结果
     */
     Result<List<RoleVO>> getRolePage(RolePageQuery query);

    /**
     * 修改角色状态
     *
     * @param id 角色ID
     * @param status 状态：0-禁用，1-启用
     */
    void updateRoleStatus(Long id, Integer status);

    /**
     * 为角色分配权限
     *
     * @param request 分配请求
     */
    void assignPermissions(AssignPermissionsRequest request);

    /**
     * 获取角色的权限ID列表
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Long> getRolePermissionIds(Long roleId);

    /**
     * 获取角色的权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<PermissionVO> getRolePermissions(Long roleId);
}
