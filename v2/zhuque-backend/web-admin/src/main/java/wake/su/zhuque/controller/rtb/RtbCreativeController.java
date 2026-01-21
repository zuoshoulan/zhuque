package wake.su.zhuque.controller.rtb;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import wake.su.zhuque.common.core.result.PageInfo;
import wake.su.zhuque.common.core.result.Result;
import wake.su.zhuque.model.dto.CreativeCreateRequest;
import wake.su.zhuque.model.dto.CreativeQueryRequest;
import wake.su.zhuque.model.dto.PageResult;
import wake.su.zhuque.model.dto.CreativeUpdateRequest;
import wake.su.zhuque.model.vo.CreativeListVO;
import wake.su.zhuque.model.vo.CreativeVO;
import wake.su.zhuque.service.RtbCreativeService;

/**
 * 创意管理Controller
 *
 * @author OpenRTB
 * @version 2.6
 */
@Tag(name = "创意管理", description = "广告创意增删改查")
@RestController
@RequestMapping("/api/rtb/creative")
@RequiredArgsConstructor
public class RtbCreativeController {

    private final RtbCreativeService creativeService;

    @PostMapping
    @Operation(summary = "创建创意")
    public Result<Long> create(@RequestBody CreativeCreateRequest request) {
        Long id = creativeService.create(request);
        return Result.success(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新创意")
    public Result<Void> update(
            @PathVariable Long id,
            @RequestBody CreativeUpdateRequest request) {
        request.setId(id);
        creativeService.update(request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除创意")
    public Result<Void> delete(@PathVariable Long id) {
        creativeService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "创意详情")
    public Result<CreativeVO> detail(@PathVariable Long id) {
        CreativeVO vo = creativeService.detail(id);
        return Result.success(vo);
    }

    @PostMapping("/list")
    @Operation(summary = "创意列表(分页)")
    public Result<java.util.List<CreativeListVO>> list(
            @RequestBody CreativeQueryRequest request) {
        PageResult<CreativeListVO> pageResult = creativeService.list(request);
        PageInfo pageInfo = PageInfo.of(pageResult.getCurrent(), pageResult.getSize(), pageResult.getTotal());
        return Result.success(pageResult.getRecords(), pageInfo);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新创意状态")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        creativeService.updateStatus(id, status);
        return Result.success();
    }
}
