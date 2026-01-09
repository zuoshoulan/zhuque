package wake.su.zhuque.service.api;

import wake.su.zhuque.model.vo.MenuVO;

import java.util.List;

/**
 * 菜单管理服务接口
 *
 * @author wake.su
 * @since 2026-01-09
 */
public interface MenuService {

    /**
     * 获取用户菜单树（用于前端渲染）
     *
     * @param userId 用户ID
     * @return 菜单树
     */
    List<MenuVO> getUserMenuTree(Long userId);

    /**
     * 获取所有菜单树
     *
     * @return 菜单树
     */
    List<MenuVO> getAllMenuTree();

    /**
     * 获取所有菜单列表（平铺）
     *
     * @return 菜单列表
     */
    List<MenuVO> getAllMenuList();
}
