package wake.su.zhuque.controller.rtb;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import wake.su.zhuque.common.core.result.Result;
import wake.su.zhuque.model.dto.CampaignCreateRequest;
import wake.su.zhuque.model.dto.CampaignQueryRequest;
import wake.su.zhuque.model.dto.CampaignUpdateRequest;
import wake.su.zhuque.model.vo.CampaignListVO;
import wake.su.zhuque.model.vo.CampaignVO;
import wake.su.zhuque.service.RtbCampaignService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 投放活动管理Controller 三层架构：Campaign（投放活动）→ AdGroup（广告组）→ Ad（广告）
 *
 * @author zhuque
 * @version 1.0
 */
@Tag(name = "投放活动管理", description = "投放活动增删改查")
@RestController
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
public class RtbCampaignController {

  private final RtbCampaignService campaignService;

  @PostMapping
  @Operation(summary = "创建投放活动")
  public Result<Long> create(@Validated @RequestBody CampaignCreateRequest request) {
    Long id = campaignService.create(request);
    return Result.success(id);
  }

  @PutMapping("/{id}")
  @Operation(summary = "更新投放活动")
  public Result<Void> update(@PathVariable Long id, @Validated @RequestBody CampaignUpdateRequest request) {
    campaignService.update(id, request);
    return Result.success();
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "删除投放活动（仅草稿状态可删除）")
  public Result<Void> delete(@PathVariable Long id) {
    campaignService.delete(id);
    return Result.success();
  }

  @GetMapping("/{id}")
  @Operation(summary = "投放活动详情")
  public Result<CampaignVO> detail(@PathVariable Long id) {
    CampaignVO vo = campaignService.detail(id);
    return Result.success(vo);
  }

  @PostMapping("/list")
  @Operation(summary = "投放活动列表(分页)")
  public Result<List<CampaignListVO>> list(@RequestBody CampaignQueryRequest request) {
    return campaignService.list(request);
  }

  @PutMapping("/{id}/status")
  @Operation(summary = "更新投放活动状态")
  public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
    campaignService.updateStatus(id, status);
    return Result.success();
  }

  @PostMapping("/{id}/start")
  @Operation(summary = "启动投放活动")
  public Result<Void> start(@PathVariable Long id) {
    campaignService.start(id);
    return Result.success();
  }

  @PostMapping("/{id}/pause")
  @Operation(summary = "暂停投放活动")
  public Result<Void> pause(@PathVariable Long id) {
    campaignService.pause(id);
    return Result.success();
  }
}
