package wake.su.zhuque.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "权限管理", description = "权限CRUD、树形结构查询接口")
@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    /**
     * 创建权限
     */
    @Operation(
        summary = "创建权限",
        description = "创建新的权限，支持路由、按钮、接口三种类型"
    )
    @ApiResponse(responseCode = "200", description = "创建成功", content = @Content(schema = @Schema(implementation = Long.class)))
    @ApiResponse(responseCode = "400", description = "参数错误")
    @ApiResponse(responseCode = "401", description = "未认证")
    @PostMapping
    public Result<Long> createPermission(
            @Parameter(description = "权限创建请求", required = true)
            @Valid @RequestBody PermissionCreateRequest request) {
        Long id = permissionService.createPermission(request);
        return Result.success(id);
    }

    /**
     * 更新权限
     */
    @Operation(
        summary = "更新权限",
        description = "更新权限信息"
    )
    @ApiResponse(responseCode = "200", description = "更新成功")
    @ApiResponse(responseCode = "400", description = "参数错误")
    @ApiResponse(responseCode = "404", description = "权限不存在")
    @PutMapping("/{id}")
    public Result<Void> updatePermission(
            @Parameter(description = "权限ID", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody PermissionCreateRequest request) {
        permissionService.updatePermission(id, request);
        return Result.success();
    }

    /**
     * 删除权限
     */
    @Operation(
        summary = "删除权限",
        description = "删除指定权限，如果存在子权限则无法删除"
    )
    @ApiResponse(responseCode = "200", description = "删除成功")
    @ApiResponse(responseCode = "400", description = "存在子权限，无法删除")
    @DeleteMapping("/{id}")
    public Result<Void> deletePermission(
            @Parameter(description = "权限ID", required = true, example = "1")
            @PathVariable Long id) {
        permissionService.deletePermission(id);
        return Result.success();
    }

    /**
     * 获取权限详情
     */
    @Operation(
        summary = "获取权限详情",
        description = "根据ID获取权限详细信息"
    )
    @ApiResponse(responseCode = "200", description = "查询成功")
    @ApiResponse(responseCode = "404", description = "权限不存在")
    @GetMapping("/{id}")
    public Result<PermissionVO> getPermission(
            @Parameter(description = "权限ID", required = true, example = "1")
            @PathVariable Long id) {
        PermissionVO vo = permissionService.getPermission(id);
        return Result.success(vo);
    }

    /**
     * 获取权限树
     */
    @Operation(
        summary = "获取权限树",
        description = "获取树形结构的权限列表，包含父子关系"
    )
    @ApiResponse(responseCode = "200", description = "查询成功")
    @GetMapping("/tree")
    public Result<List<PermissionVO>> getPermissionTree() {
        List<PermissionVO> tree = permissionService.getPermissionTree();
        return Result.success(tree);
    }

    /**
     * 获取权限列表（平铺）
     */
    @Operation(
        summary = "获取权限列表",
        description = "获取平铺的权限列表，不包含层级关系"
    )
    @ApiResponse(responseCode = "200", description = "查询成功")
    @GetMapping("/list")
    public Result<List<PermissionVO>> getPermissionList() {
        List<PermissionVO> list = permissionService.getPermissionList();
        return Result.success(list);
    }
}
