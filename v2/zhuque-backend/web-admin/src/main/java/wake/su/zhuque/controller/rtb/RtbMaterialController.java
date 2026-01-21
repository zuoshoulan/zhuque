package wake.su.zhuque.controller.rtb;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import wake.su.zhuque.common.core.result.PageInfo;
import wake.su.zhuque.common.core.result.Result;
import wake.su.zhuque.model.dto.MaterialCreateRequest;
import wake.su.zhuque.model.dto.MaterialQueryRequest;
import wake.su.zhuque.model.dto.PageResult;
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
    public Result<Long> create(@RequestBody MaterialCreateRequest request) {
        Long id = materialService.create(request);
        return Result.success(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新素材")
    public Result<Void> update(
            @PathVariable Long id,
            @RequestBody MaterialUpdateRequest request) {
        request.setId(id);
        materialService.update(request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除素材")
    public Result<Void> delete(@PathVariable Long id) {
        materialService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "素材详情")
    public Result<MaterialVO> detail(@PathVariable Long id) {
        MaterialVO vo = materialService.detail(id);
        return Result.success(vo);
    }

    @PostMapping("/list")
    @Operation(summary = "素材列表(分页)")
    public Result<java.util.List<MaterialListVO>> list(
            @RequestBody MaterialQueryRequest request) {
        PageResult<MaterialListVO> pageResult = materialService.list(request);
        PageInfo pageInfo = PageInfo.of(pageResult.getCurrent(), pageResult.getSize(), pageResult.getTotal());
        return Result.success(pageResult.getRecords(), pageInfo);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新素材状态")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        materialService.updateStatus(id, status);
        return Result.success();
    }
}
