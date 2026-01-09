package wake.su.zhuque.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import wake.su.zhuque.common.core.result.Result;
import wake.su.zhuque.model.dto.PermissionCreateRequest;
import wake.su.zhuque.model.vo.PermissionVO;
import wake.su.zhuque.service.api.PermissionService;

import java.util.List;

/**
 * 权限管理Controller
 *
 * @author wake.su
 * @since 2026-01-09
 */
@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    /**
     * 创建权限
     */
    @PostMapping
    public Result<Long> createPermission(@Valid @RequestBody PermissionCreateRequest request) {
        Long id = permissionService.createPermission(request);
        return Result.success(id);
    }

    /**
     * 更新权限
     */
    @PutMapping("/{id}")
    public Result<Void> updatePermission(
            @PathVariable Long id,
            @Valid @RequestBody PermissionCreateRequest request) {
        permissionService.updatePermission(id, request);
        return Result.success();
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/{id}")
    public Result<Void> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return Result.success();
    }

    /**
     * 获取权限详情
     */
    @GetMapping("/{id}")
    public Result<PermissionVO> getPermission(@PathVariable Long id) {
        PermissionVO vo = permissionService.getPermission(id);
        return Result.success(vo);
    }

    /**
     * 获取权限树
     */
    @GetMapping("/tree")
    public Result<List<PermissionVO>> getPermissionTree() {
        List<PermissionVO> tree = permissionService.getPermissionTree();
        return Result.success(tree);
    }

    /**
     * 获取权限列表（平铺）
     */
    @GetMapping("/list")
    public Result<List<PermissionVO>> getPermissionList() {
        List<PermissionVO> list = permissionService.getPermissionList();
        return Result.success(list);
    }
}
