package wake.su.zhuque.service.api;

import wake.su.zhuque.model.dto.PermissionCreateRequest;
import wake.su.zhuque.model.vo.PermissionVO;

import java.util.List;

/**
 * 权限管理服务接口
 *
 * @author wake.su
 * @since 2026-01-09
 */
public interface PermissionService {

    /**
     * 创建权限
     *
     * @param request 创建请求
     * @return 权限ID
     */
    Long createPermission(PermissionCreateRequest request);

    /**
     * 更新权限
     *
     * @param id 权限ID
     * @param request 更新请求
     */
    void updatePermission(Long id, PermissionCreateRequest request);

    /**
     * 删除权限
     *
     * @param id 权限ID
     */
    void deletePermission(Long id);

    /**
     * 获取权限详情
     *
     * @param id 权限ID
     * @return 权限VO
     */
    PermissionVO getPermission(Long id);

    /**
     * 获取权限列表（树形结构）
     *
     * @return 权限树
     */
    List<PermissionVO> getPermissionTree();

    /**
     * 获取权限列表（平铺）
     *
     * @return 权限列表
     */
    List<PermissionVO> getPermissionList();

    /**
     * 获取用户权限编码列表
     *
     * @param userId 用户ID
     * @return 权限编码列表
     */
    List<String> getUserPermissionCodes(Long userId);

    /**
     * 检查用户是否拥有指定权限
     *
     * @param userId 用户ID
     * @param permissionCode 权限编码
     * @return 是否拥有权限
     */
    boolean hasPermission(Long userId, String permissionCode);

    /**
     * 检查用户是否拥有指定权限（支持多个）
     *
     * @param userId 用户ID
     * @param permissionCodes 权限编码列表
     * @param requireAll 是否需要全部拥有
     * @return 是否拥有权限
     */
    boolean hasPermissions(Long userId, List<String> permissionCodes, boolean requireAll);
}
