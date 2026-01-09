package wake.su.zhuque.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import wake.su.zhuque.common.core.result.Result;
import wake.su.zhuque.model.vo.MenuVO;
import wake.su.zhuque.service.api.MenuService;

import java.util.List;

/**
 * 菜单管理Controller
 *
 * @author wake.su
 * @since 2026-01-09
 */
@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    /**
     * 获取当前用户的菜单树
     */
    @GetMapping("/user/tree")
    public Result<List<MenuVO>> getUserMenuTree() {
        // TODO: 从当前登录用户获取userId
        Long userId = 1L; // 临时硬编码
        List<MenuVO> tree = menuService.getUserMenuTree(userId);
        return Result.success(tree);
    }

    /**
     * 获取所有菜单树
     */
    @GetMapping("/tree")
    public Result<List<MenuVO>> getAllMenuTree() {
        List<MenuVO> tree = menuService.getAllMenuTree();
        return Result.success(tree);
    }

    /**
     * 获取所有菜单列表（平铺）
     */
    @GetMapping("/list")
    public Result<List<MenuVO>> getAllMenuList() {
        List<MenuVO> list = menuService.getAllMenuList();
        return Result.success(list);
    }
}
