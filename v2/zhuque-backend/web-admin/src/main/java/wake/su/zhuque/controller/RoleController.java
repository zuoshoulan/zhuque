package wake.su.zhuque.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import wake.su.zhuque.common.core.result.PageInfo;
import wake.su.zhuque.common.core.result.Result;
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
    public Result<Long> createRole(@Valid @RequestBody RoleCreateRequest request) {
        Long id = roleService.createRole(request);
        return Result.success(id);
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    public Result<Void> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleCreateRequest request) {
        roleService.updateRole(id, request);
        return Result.success();
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success();
    }

    /**
     * 获取角色详情
     */
    @GetMapping("/{id}")
    public Result<RoleVO> getRole(@PathVariable Long id) {
        RoleVO vo = roleService.getRole(id);
        return Result.success(vo);
    }

    /**
     * 获取角色列表
     */
    @GetMapping("/list")
    public Result<List<RoleVO>> getRoleList() {
        List<RoleVO> list = roleService.getRoleList();
        return Result.success(list);
    }

    /**
     * 分页查询角色列表
     */
    @GetMapping("/page")
    public Result<List<RoleVO>> getRolePage(RolePageQuery query) {
        PageResult<RoleVO> pageResult = roleService.getRolePage(query);
        PageInfo pageInfo = PageInfo.of(pageResult.getCurrent(), pageResult.getSize(), pageResult.getTotal());
        return Result.success(pageResult.getRecords(), pageInfo);
    }

    /**
     * 修改角色状态
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateRoleStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        Integer status = request.get("status");
        roleService.updateRoleStatus(id, status);
        return Result.success();
    }

    /**
     * 为角色分配权限
     */
    @PostMapping("/{id}/permissions")
    public Result<Void> assignPermissions(
            @PathVariable Long id,
            @Valid @RequestBody AssignPermissionsRequest request) {
        request.setRoleId(id);
        roleService.assignPermissions(request);
        return Result.success();
    }

    /**
     * 获取角色的权限ID列表
     */
    @GetMapping("/{id}/permission-ids")
    public Result<List<Long>> getRolePermissionIds(@PathVariable Long id) {
        List<Long> ids = roleService.getRolePermissionIds(id);
        return Result.success(ids);
    }

    /**
     * 获取角色的权限列表
     */
    @GetMapping("/{id}/permissions")
    public Result<List<PermissionVO>> getRolePermissions(@PathVariable Long id) {
        List<PermissionVO> permissions = roleService.getRolePermissions(id);
        return Result.success(permissions);
    }
}
