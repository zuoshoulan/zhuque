package wake.su.zhuque.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import wake.su.zhuque.common.core.result.Result;
import wake.su.zhuque.common.security.annotation.RequiresPermission;

/**
 * 广告主管理Controller - 使用@RequiresPermission注解示例
 *
 * @author wake.su
 * @since 2026-01-09
 */
@RestController
@RequestMapping("/api/advertisers")
@RequiredArgsConstructor
public class AdvertiserController {

    /**
     * 创建广告主
     * 需要权限：advertiser:create
     */
    @PostMapping
    @RequiresPermission("advertiser:create")
    public Result<String> createAdvertiser(@RequestBody String request) {
        // 业务逻辑
        return Result.success("创建广告主成功");
    }

    /**
     * 更新广告主
     * 需要权限：advertiser:update
     */
    @PutMapping("/{id}")
    @RequiresPermission("advertiser:update")
    public Result<String> updateAdvertiser(@PathVariable Long id, @RequestBody String request) {
        // 业务逻辑
        return Result.success("更新广告主成功");
    }

    /**
     * 删除广告主
     * 需要权限：advertiser:delete
     */
    @DeleteMapping("/{id}")
    @RequiresPermission("advertiser:delete")
    public Result<String> deleteAdvertiser(@PathVariable Long id) {
        // 业务逻辑
        return Result.success("删除广告主成功");
    }

    /**
     * 审核广告主
     * 需要权限：advertiser:audit
     */
    @PutMapping("/{id}/audit")
    @RequiresPermission("advertiser:audit")
    public Result<String> auditAdvertiser(@PathVariable Long id) {
        // 业务逻辑
        return Result.success("审核广告主成功");
    }

    /**
     * 查看广告主详情
     * 需要权限：advertiser:query
     */
    @GetMapping("/{id}")
    @RequiresPermission("advertiser:query")
    public Result<String> getAdvertiser(@PathVariable Long id) {
        // 业务逻辑
        return Result.success("获取广告主详情成功");
    }

    /**
     * 广告主列表
     * 需要权限：advertiser:query
     */
    @GetMapping
    @RequiresPermission("advertiser:query")
    public Result<String> listAdvertisers() {
        // 业务逻辑
        return Result.success("获取广告主列表成功");
    }

    /**
     * 示例：需要多个权限之一（OR关系）
     * 拥有 advertiser:create 或 advertiser:audit 任一权限即可
     */
    @PostMapping("/batch")
    @RequiresPermission(value = {"advertiser:create", "advertiser:audit"}, logical = RequiresPermission.LogicalType.OR)
    public Result<String> batchOperation() {
        // 业务逻辑
        return Result.success("批量操作成功");
    }

    /**
     * 示例：需要同时拥有多个权限（AND关系）
     * 必须同时拥有 advertiser:create 和 advertiser:audit 两个权限
     */
    @PostMapping("/special")
    @RequiresPermission(value = {"advertiser:create", "advertiser:audit"}, logical = RequiresPermission.LogicalType.AND)
    public Result<String> specialOperation() {
        // 业务逻辑
        return Result.success("特殊操作成功");
    }
}
