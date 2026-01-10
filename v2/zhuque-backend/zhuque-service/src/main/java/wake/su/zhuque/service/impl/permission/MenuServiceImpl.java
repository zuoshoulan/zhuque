package wake.su.zhuque.service.impl.permission;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wake.su.zhuque.dao.mapper.SysMenuMapper;
import wake.su.zhuque.dao.mapper.SysUserRoleMapper;
import wake.su.zhuque.model.entity.SysMenuDO;
import wake.su.zhuque.model.entity.SysUserRoleDO;
import wake.su.zhuque.model.vo.MenuVO;
import wake.su.zhuque.service.api.MenuService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单管理服务实现
 *
 * @author wake.su
 * @since 2026-01-09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final SysMenuMapper menuMapper;
    private final SysUserRoleMapper userRoleMapper;

    @Override
    public List<MenuVO> getUserMenuTree(Long userId) {
        // 查询用户的所有角色
        List<SysUserRoleDO> userRoles = userRoleMapper.selectList(
            new LambdaQueryWrapper<SysUserRoleDO>()
                .eq(SysUserRoleDO::getUserId, userId)
        );

        // TODO: 根据角色查询菜单权限
        // 简化实现：返回所有启用的菜单
        List<SysMenuDO> allMenus = menuMapper.selectList(
            new LambdaQueryWrapper<SysMenuDO>()
                .eq(SysMenuDO::getStatus, 1)
                .eq(SysMenuDO::getVisible, 1)
                .orderByAsc(SysMenuDO::getSortOrder)
        );

        List<MenuVO> voList = allMenus.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());

        return buildTree(voList, 0L);
    }

    @Override
    public List<MenuVO> getAllMenuTree() {
        List<SysMenuDO> allMenus = menuMapper.selectList(
            new LambdaQueryWrapper<SysMenuDO>()
                .eq(SysMenuDO::getStatus, 1)
                .orderByAsc(SysMenuDO::getSortOrder)
        );

        List<MenuVO> voList = allMenus.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());

        return buildTree(voList, 0L);
    }

    @Override
    public List<MenuVO> getAllMenuList() {
        List<SysMenuDO> allMenus = menuMapper.selectList(
            new LambdaQueryWrapper<SysMenuDO>()
                .eq(SysMenuDO::getStatus, 1)
                .orderByAsc(SysMenuDO::getSortOrder)
        );

        return allMenus.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    /**
     * 构建树形结构
     */
    private List<MenuVO> buildTree(List<MenuVO> all, Long parentId) {
        return all.stream()
            .filter(vo -> vo.getParentId().equals(parentId))
            .peek(vo -> {
                List<MenuVO> children = buildTree(all, vo.getId());
                vo.setChildren(children.isEmpty() ? null : children);
            })
            .collect(Collectors.toList());
    }

    /**
     * 转换为VO
     */
    private MenuVO convertToVO(SysMenuDO entity) {
        MenuVO vo = new MenuVO();
        vo.setId(entity.getId());
        vo.setParentId(entity.getParentId());
        vo.setMenuName(entity.getMenuName());
        vo.setMenuType(entity.getMenuType());
        vo.setIcon(entity.getIcon());
        vo.setPath(entity.getPath());
        vo.setComponent(entity.getComponent());
        vo.setPermissionCode(entity.getPermissionCode());
        vo.setSortOrder(entity.getSortOrder());
        vo.setVisible(entity.getVisible());
        vo.setStatus(entity.getStatus());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}
