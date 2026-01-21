package wake.su.zhuque.controller.rtb;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import wake.su.zhuque.common.core.result.OldResult;
import wake.su.zhuque.model.dto.MaterialCreateRequest;
import wake.su.zhuque.model.dto.MaterialQueryRequest;
import wake.su.zhuque.model.dto.MaterialUpdateRequest;
import wake.su.zhuque.model.vo.MaterialListVO;
import wake.su.zhuque.model.vo.MaterialVO;
import wake.su.zhuque.service.RtbMaterialService;

/**
 * 素材管理Controller
 *
 * @author OpenRTB
 * @version 2.6
 */
@Tag(name = "素材管理", description = "广告素材增删改查")
@RestController
@RequestMapping("/api/rtb/material")
@RequiredArgsConstructor
public class RtbMaterialController {

    private final RtbMaterialService materialService;

    @PostMapping
    @Operation(summary = "创建素材")
    public OldResult<Long> create(@RequestBody MaterialCreateRequest request) {
        Long id = materialService.create(request);
        return OldResult.success(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新素材")
    public OldResult<Void> update(
            @PathVariable Long id,
            @RequestBody MaterialUpdateRequest request) {
        request.setId(id);
        materialService.update(request);
        return OldResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除素材")
    public OldResult<Void> delete(@PathVariable Long id) {
        materialService.delete(id);
        return OldResult.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "素材详情")
    public OldResult<MaterialVO> detail(@PathVariable Long id) {
        MaterialVO vo = materialService.detail(id);
        return OldResult.success(vo);
    }

    @PostMapping("/list")
    @Operation(summary = "素材列表(分页)")
    public OldResult<wake.su.zhuque.model.dto.PageResult<MaterialListVO>> list(
            @RequestBody MaterialQueryRequest request) {
        return OldResult.success(materialService.list(request));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新素材状态")
    public OldResult<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        materialService.updateStatus(id, status);
        return OldResult.success();
    }
}
