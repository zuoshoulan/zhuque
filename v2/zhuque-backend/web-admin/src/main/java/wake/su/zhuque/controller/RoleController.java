package wake.su.zhuque.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import wake.su.zhuque.common.core.result.OldResult;
import wake.su.zhuque.model.dto.AssignPermissionsRequest;
import wake.su.zhuque.model.dto.PageResult;
import wake.su.zhuque.model.dto.RoleCreateRequest;
import wake.su.zhuque.model.query.RolePageQuery;
import wake.su.zhuque.model.vo.PermissionVO;
import wake.su.zhuque.model.vo.RoleVO;
import wake.su.zhuque.service.api.RoleService;

import java.util.List;
import java.util.Map;

/**
 * 角色管理Controller
 *
 * @author wake.su
 * @since 2026-01-09
 */
@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * 创建角色
     */
    @PostMapping
    public OldResult<Long> createRole(@Valid @RequestBody RoleCreateRequest request) {
        Long id = roleService.createRole(request);
        return OldResult.success(id);
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    public OldResult<Void> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleCreateRequest request) {
        roleService.updateRole(id, request);
        return OldResult.success();
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public OldResult<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return OldResult.success();
    }

    /**
     * 获取角色详情
     */
    @GetMapping("/{id}")
    public OldResult<RoleVO> getRole(@PathVariable Long id) {
        RoleVO vo = roleService.getRole(id);
        return OldResult.success(vo);
    }

    /**
     * 获取角色列表
     */
    @GetMapping("/list")
    public OldResult<List<RoleVO>> getRoleList() {
        List<RoleVO> list = roleService.getRoleList();
        return OldResult.success(list);
    }

    /**
     * 分页查询角色列表
     */
    @GetMapping("/page")
    public OldResult<PageResult<RoleVO>> getRolePage(RolePageQuery query) {
        PageResult<RoleVO> pageResult = roleService.getRolePage(query);
        return OldResult.success(pageResult);
    }

    /**
     * 修改角色状态
     */
    @PutMapping("/{id}/status")
    public OldResult<Void> updateRoleStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        Integer status = request.get("status");
        roleService.updateRoleStatus(id, status);
        return OldResult.success();
    }

    /**
     * 为角色分配权限
     */
    @PostMapping("/{id}/permissions")
    public OldResult<Void> assignPermissions(
            @PathVariable Long id,
            @Valid @RequestBody AssignPermissionsRequest request) {
        request.setRoleId(id);
        roleService.assignPermissions(request);
        return OldResult.success();
    }

    /**
     * 获取角色的权限ID列表
     */
    @GetMapping("/{id}/permission-ids")
    public OldResult<List<Long>> getRolePermissionIds(@PathVariable Long id) {
        List<Long> ids = roleService.getRolePermissionIds(id);
        return OldResult.success(ids);
    }

    /**
     * 获取角色的权限列表
     */
    @GetMapping("/{id}/permissions")
    public OldResult<List<PermissionVO>> getRolePermissions(@PathVariable Long id) {
        List<PermissionVO> permissions = roleService.getRolePermissions(id);
        return OldResult.success(permissions);
    }
}
