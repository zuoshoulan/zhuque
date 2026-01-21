package wake.su.zhuque.controller;

import org.springframework.web.bind.annotation.*;
import wake.su.zhuque.common.core.result.OldResult;
import wake.su.zhuque.common.security.annotation.RequiresApiPermission;
import wake.su.zhuque.common.security.annotation.RequiresPermission;

/**
 * 广告主管理Controller - 三层权限控制示例
 *
 * 权限说明：
 * 1. 菜单权限：advertiser - 控制页面可见性
 * 2. 按钮权限：advertiser:create, advertiser:update等 - 控制按钮可见性
 * 3. 接口权限：api:advertiser:create, api:advertiser:update等 - 控制API访问权限
 *
 * @author wake.su
 * @since 2026-01-09
 */
@RestController
@RequestMapping("/api/advertisers")
public class AdvertiserController {

    /**
     * 创建广告主
     *
     * 权限说明：
     * - 前端按钮：需要 advertiser:create 权限（第二层）
     * - 后端API：需要 api:advertiser:create 权限（第三层）
     *
     * 设计理念：
     * 前端控制按钮是否显示，后端控制接口是否可调用
     * 两层权限可以独立配置，实现更细粒度的控制
     */
    @PostMapping
    @RequiresApiPermission("api:advertiser:create")
    public OldResult<String> createAdvertiser(@RequestBody String request) {
        // 业务逻辑
        return OldResult.success("创建广告主成功");
    }

    /**
     * 更新广告主
     *
     * 权限说明：
     * - 前端按钮：需要 advertiser:update 权限（第二层）
     * - 后端API：需要 api:advertiser:update 权限（第三层）
     */
    @PutMapping("/{id}")
    @RequiresApiPermission("api:advertiser:update")
    public OldResult<String> updateAdvertiser(@PathVariable Long id, @RequestBody String request) {
        // 业务逻辑
        return OldResult.success("更新广告主成功");
    }

    /**
     * 删除广告主
     *
     * 权限说明：
     * - 前端按钮：需要 advertiser:delete 权限（第二层）
     * - 后端API：需要 api:advertiser:delete 权限（第三层）
     */
    @DeleteMapping("/{id}")
    @RequiresApiPermission("api:advertiser:delete")
    public OldResult<String> deleteAdvertiser(@PathVariable Long id) {
        // 业务逻辑
        return OldResult.success("删除广告主成功");
    }

    /**
     * 审核广告主
     *
     * 权限说明：
     * - 前端按钮：需要 advertiser:audit 权限（第二层）
     * - 后端API：需要 api:advertiser:audit 权限（第三层，如果配置了的话）
     */
    @PutMapping("/{id}/audit")
    @RequiresPermission("advertiser:audit")
    public OldResult<String> auditAdvertiser(@PathVariable Long id) {
        // 业务逻辑
        return OldResult.success("审核广告主成功");
    }

    /**
     * 查看广告主详情
     *
     * 权限说明：
     * - 前端按钮：需要 advertiser:query 权限（第二层）
     * - 后端API：需要 api:advertiser:detail 权限（第三层）
     */
    @GetMapping("/{id}")
    @RequiresApiPermission("api:advertiser:detail")
    public OldResult<String> getAdvertiser(@PathVariable Long id) {
        // 业务逻辑
        return OldResult.success("获取广告主详情成功");
    }

    /**
     * 广告主列表
     *
     * 权限说明：
     * - 前端菜单：需要 advertiser 权限（第一层）
     * - 后端API：需要 api:advertiser:list 权限（第三层）
     */
    @GetMapping
    @RequiresApiPermission("api:advertiser:list")
    public OldResult<String> listAdvertisers() {
        // 业务逻辑
        return OldResult.success("获取广告主列表成功");
    }

    /**
     * 示例：需要多个API权限之一（OR关系）
     * 拥有 api:advertiser:create 或 api:advertiser:audit 任一权限即可
     */
    @PostMapping("/batch")
    @RequiresApiPermission(value = {"api:advertiser:create", "api:advertiser:audit"}, logical = RequiresApiPermission.LogicalType.OR)
    public OldResult<String> batchOperation() {
        // 业务逻辑
        return OldResult.success("批量操作成功");
    }

    /**
     * 示例：需要同时拥有多个API权限（AND关系）
     * 必须同时拥有 api:advertiser:create 和 api:advertiser:audit 两个权限
     */
    @PostMapping("/special")
    @RequiresApiPermission(value = {"api:advertiser:create", "api:advertiser:audit"}, logical = RequiresApiPermission.LogicalType.AND)
    public OldResult<String> specialOperation() {
        // 业务逻辑
        return OldResult.success("特殊操作成功");
    }
}
