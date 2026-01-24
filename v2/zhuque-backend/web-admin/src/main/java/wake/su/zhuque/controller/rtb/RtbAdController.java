package wake.su.zhuque.controller.rtb;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import wake.su.zhuque.common.core.result.Result;
import wake.su.zhuque.model.dto.AdCreateRequest;
import wake.su.zhuque.model.dto.AdQueryRequest;
import wake.su.zhuque.model.dto.AdUpdateRequest;
import wake.su.zhuque.model.vo.AdListVO;
import wake.su.zhuque.model.vo.AdVO;
import wake.su.zhuque.service.RtbAdService;

import java.util.List;

/**
 * 广告管理Controller
 * 三层架构：Campaign（投放活动）→ AdGroup（广告组）→ Ad（广告）
 *
 * @author zhuque
 * @version 1.0
 */
@Tag(name = "广告管理", description = "广告增删改查")
@RestController
@RequestMapping("/api/ads")
@RequiredArgsConstructor
public class RtbAdController {

    private final RtbAdService adService;

    @PostMapping
    @Operation(summary = "创建广告")
    public Result<Long> create(@Validated @RequestBody AdCreateRequest request) {
        Long id = adService.create(request);
        return Result.success(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新广告")
    public Result<Void> update(
            @PathVariable Long id,
            @Validated @RequestBody AdUpdateRequest request) {
        adService.update(id, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除广告（仅草稿状态可删除）")
    public Result<Void> delete(@PathVariable Long id) {
        adService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "广告详情")
    public Result<AdVO> detail(@PathVariable Long id) {
        AdVO vo = adService.detail(id);
        return Result.success(vo);
    }

    @PostMapping("/list")
    @Operation(summary = "广告列表(分页)")
    public Result<List<AdListVO>> list(@RequestBody AdQueryRequest request) {
        return adService.list(request);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新广告状态")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        adService.updateStatus(id, status);
        return Result.success();
    }

    @PostMapping("/{id}/start")
    @Operation(summary = "启动广告")
    public Result<Void> start(@PathVariable Long id) {
        adService.start(id);
        return Result.success();
    }

    @PostMapping("/{id}/pause")
    @Operation(summary = "暂停广告")
    public Result<Void> pause(@PathVariable Long id) {
        adService.pause(id);
        return Result.success();
    }

    @GetMapping("/ad-group/{adGroupId}")
    @Operation(summary = "根据广告组ID查询广告列表")
    public Result<List<AdListVO>> listByAdGroupId(@PathVariable Long adGroupId) {
        List<AdListVO> list = adService.listByAdGroupId(adGroupId);
        return Result.success(list);
    }
}
