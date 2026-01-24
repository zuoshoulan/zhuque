package wake.su.zhuque.controller.rtb;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import wake.su.zhuque.common.core.result.Result;
import wake.su.zhuque.model.dto.AdGroupCreateRequest;
import wake.su.zhuque.model.dto.AdGroupQueryRequest;
import wake.su.zhuque.model.dto.AdGroupUpdateRequest;
import wake.su.zhuque.model.vo.AdGroupListVO;
import wake.su.zhuque.model.vo.AdGroupVO;
import wake.su.zhuque.service.RtbAdGroupService;

import java.util.List;

/**
 * 广告组管理Controller
 * 三层架构：Campaign（投放活动）→ AdGroup（广告组）→ Ad（广告）
 *
 * @author zhuque
 * @version 1.0
 */
@Tag(name = "广告组管理", description = "广告组增删改查")
@RestController
@RequestMapping("/api/ad-groups")
@RequiredArgsConstructor
public class RtbAdGroupController {

    private final RtbAdGroupService adGroupService;

    @PostMapping
    @Operation(summary = "创建广告组")
    public Result<Long> create(@Validated @RequestBody AdGroupCreateRequest request) {
        Long id = adGroupService.create(request);
        return Result.success(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新广告组")
    public Result<Void> update(
            @PathVariable Long id,
            @Validated @RequestBody AdGroupUpdateRequest request) {
        adGroupService.update(id, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除广告组（仅草稿状态可删除）")
    public Result<Void> delete(@PathVariable Long id) {
        adGroupService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "广告组详情")
    public Result<AdGroupVO> detail(@PathVariable Long id) {
        AdGroupVO vo = adGroupService.detail(id);
        return Result.success(vo);
    }

    @PostMapping("/list")
    @Operation(summary = "广告组列表(分页)")
    public Result<List<AdGroupListVO>> list(@RequestBody AdGroupQueryRequest request) {
        return adGroupService.list(request);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新广告组状态")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        adGroupService.updateStatus(id, status);
        return Result.success();
    }

    @PostMapping("/{id}/start")
    @Operation(summary = "启动广告组")
    public Result<Void> start(@PathVariable Long id) {
        adGroupService.start(id);
        return Result.success();
    }

    @PostMapping("/{id}/pause")
    @Operation(summary = "暂停广告组")
    public Result<Void> pause(@PathVariable Long id) {
        adGroupService.pause(id);
        return Result.success();
    }

    @GetMapping("/campaign/{campaignId}")
    @Operation(summary = "根据投放活动ID查询广告组列表")
    public Result<List<AdGroupListVO>> listByCampaignId(@PathVariable Long campaignId) {
        List<AdGroupListVO> list = adGroupService.listByCampaignId(campaignId);
        return Result.success(list);
    }
}
