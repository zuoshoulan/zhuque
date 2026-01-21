package wake.su.zhuque.controller.rtb;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import wake.su.zhuque.common.core.result.OldResult;
import wake.su.zhuque.model.dto.CreativeCreateRequest;
import wake.su.zhuque.model.dto.CreativeQueryRequest;
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
    public OldResult<Long> create(@RequestBody CreativeCreateRequest request) {
        Long id = creativeService.create(request);
        return OldResult.success(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新创意")
    public OldResult<Void> update(
            @PathVariable Long id,
            @RequestBody CreativeUpdateRequest request) {
        request.setId(id);
        creativeService.update(request);
        return OldResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除创意")
    public OldResult<Void> delete(@PathVariable Long id) {
        creativeService.delete(id);
        return OldResult.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "创意详情")
    public OldResult<CreativeVO> detail(@PathVariable Long id) {
        CreativeVO vo = creativeService.detail(id);
        return OldResult.success(vo);
    }

    @PostMapping("/list")
    @Operation(summary = "创意列表(分页)")
    public OldResult<wake.su.zhuque.model.dto.PageResult<CreativeListVO>> list(
            @RequestBody CreativeQueryRequest request) {
        return OldResult.success(creativeService.list(request));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新创意状态")
    public OldResult<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        creativeService.updateStatus(id, status);
        return OldResult.success();
    }
}
