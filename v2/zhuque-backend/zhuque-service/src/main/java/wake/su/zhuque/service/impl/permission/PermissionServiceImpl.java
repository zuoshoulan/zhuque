package wake.su.zhuque.service.impl.permission;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wake.su.zhuque.dao.mapper.SysPermissionMapper;
import wake.su.zhuque.dao.mapper.SysRoleMapper;
import wake.su.zhuque.dao.mapper.SysRolePermissionMapper;
import wake.su.zhuque.dao.mapper.SysUserRoleMapper;
import wake.su.zhuque.model.dto.PermissionCreateRequest;
import wake.su.zhuque.model.entity.SysPermissionDO;
import wake.su.zhuque.model.entity.SysRolePermissionDO;
import wake.su.zhuque.model.entity.SysUserRoleDO;
import wake.su.zhuque.model.vo.PermissionVO;
import wake.su.zhuque.service.api.PermissionService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限管理服务实现
 *
 * @author wake.su
 * @since 2026-01-09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final SysPermissionMapper permissionMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysRolePermissionMapper rolePermissionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPermission(PermissionCreateRequest request) {
        // 检查权限编码是否已存在
        SysPermissionDO existing = permissionMapper.selectOne(
            new LambdaQueryWrapper<SysPermissionDO>()
                .eq(SysPermissionDO::getPermissionCode, request.getPermissionCode())
        );
        if (existing != null) {
            throw new RuntimeException("权限编码已存在：" + request.getPermissionCode());
        }

        SysPermissionDO permission = new SysPermissionDO();
        permission.setParentId(request.getParentId() != null ? request.getParentId() : 0L);
        permission.setPermissionCode(request.getPermissionCode());
        permission.setPermissionName(request.getPermissionName());
        permission.setPermissionType(request.getPermissionType());
        permission.setPath(request.getPath());
        permission.setMethod(request.getMethod());
        permission.setIcon(request.getIcon());
        permission.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        permission.setStatus(1);

        permissionMapper.insert(permission);
        log.info("创建权限成功：{}", permission.getPermissionCode());
        return permission.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePermission(Long id, PermissionCreateRequest request) {
        SysPermissionDO permission = permissionMapper.selectById(id);
        if (permission == null) {
            throw new RuntimeException("权限不存在");
        }

        permission.setPermissionName(request.getPermissionName());
        permission.setPermissionType(request.getPermissionType());
        permission.setPath(request.getPath());
        permission.setMethod(request.getMethod());
        permission.setIcon(request.getIcon());
        permission.setSortOrder(request.getSortOrder());
        permission.setUpdateBy(getCurrentUsername());  // 必须显式设置更新人
        permission.setUpdateTime(LocalDateTime.now());  // 必须显式设置更新时间

        permissionMapper.updateById(permission);
        log.info("更新权限成功：{}", permission.getPermissionCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePermission(Long id) {
        // 检查是否有子权限
        Long childCount = permissionMapper.selectCount(
            new LambdaQueryWrapper<SysPermissionDO>()
                .eq(SysPermissionDO::getParentId, id)
        );
        if (childCount > 0) {
            throw new RuntimeException("存在子权限，无法删除");
        }

        permissionMapper.deleteById(id);
        log.info("删除权限成功：{}", id);
    }

    @Override
    public PermissionVO getPermission(Long id) {
        SysPermissionDO permission = permissionMapper.selectById(id);
        if (permission == null) {
            return null;
        }
        return convertToVO(permission);
    }

    @Override
    public List<PermissionVO> getPermissionTree() {
        List<SysPermissionDO> all = permissionMapper.selectList(
            new LambdaQueryWrapper<SysPermissionDO>()
                .orderByAsc(SysPermissionDO::getSortOrder)
        );

        List<PermissionVO> voList = all.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());

        return buildTree(voList, 0L);
    }

    @Override
    public List<PermissionVO> getPermissionList() {
        List<SysPermissionDO> all = permissionMapper.selectList(
            new LambdaQueryWrapper<SysPermissionDO>()
                .orderByAsc(SysPermissionDO::getSortOrder)
        );
        return all.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    @Override
    public List<String> getUserPermissionCodes(Long userId) {
        // 查询用户的所有角色
        List<SysUserRoleDO> userRoles = userRoleMapper.selectList(
            new LambdaQueryWrapper<SysUserRoleDO>()
                .eq(SysUserRoleDO::getUserId, userId)
        );

        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> roleIds = userRoles.stream()
            .map(SysUserRoleDO::getRoleId)
            .collect(Collectors.toList());

        // 查询角色的所有权限
        List<SysRolePermissionDO> rolePermissions = rolePermissionMapper.selectList(
            new LambdaQueryWrapper<SysRolePermissionDO>()
                .in(SysRolePermissionDO::getRoleId, roleIds)
        );

        if (rolePermissions.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> permissionIds = rolePermissions.stream()
            .map(SysRolePermissionDO::getPermissionId)
            .distinct()
            .collect(Collectors.toList());

        // 查询权限详情
        List<SysPermissionDO> permissions = permissionMapper.selectList(
            new LambdaQueryWrapper<SysPermissionDO>()
                .in(SysPermissionDO::getId, permissionIds)
                .eq(SysPermissionDO::getStatus, 1)
        );

        return permissions.stream()
            .map(SysPermissionDO::getPermissionCode)
            .collect(Collectors.toList());
    }

    @Override
    public boolean hasPermission(Long userId, String permissionCode) {
        if (userId == null || permissionCode == null) {
            return false;
        }

        List<String> userPermissions = getUserPermissionCodes(userId);
        return userPermissions.contains(permissionCode);
    }

    @Override
    public boolean hasPermissions(Long userId, List<String> permissionCodes, boolean requireAll) {
        if (userId == null || permissionCodes == null || permissionCodes.isEmpty()) {
            return false;
        }

        List<String> userPermissions = getUserPermissionCodes(userId);

        if (requireAll) {
            // 需要拥有所有权限
            return userPermissions.containsAll(permissionCodes);
        } else {
            // 拥有任一权限即可
            return permissionCodes.stream().anyMatch(userPermissions::contains);
        }
    }

    /**
     * 构建树形结构
     */
    private List<PermissionVO> buildTree(List<PermissionVO> all, Long parentId) {
        return all.stream()
            .filter(vo -> vo.getParentId().equals(parentId))
            .peek(vo -> {
                List<PermissionVO> children = buildTree(all, vo.getId());
                vo.setChildren(children.isEmpty() ? null : children);
            })
            .collect(Collectors.toList());
    }

    /**
     * 转换为VO
     */
    private PermissionVO convertToVO(SysPermissionDO entity) {
        PermissionVO vo = new PermissionVO();
        vo.setId(entity.getId());
        vo.setParentId(entity.getParentId());
        vo.setPermissionCode(entity.getPermissionCode());
        vo.setPermissionName(entity.getPermissionName());
        vo.setPermissionType(entity.getPermissionType());
        vo.setPath(entity.getPath());
        vo.setMethod(entity.getMethod());
        vo.setIcon(entity.getIcon());
        vo.setSortOrder(entity.getSortOrder());
        vo.setStatus(entity.getStatus());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }

    /**
     * 获取当前登录用户名
     */
    private String getCurrentUsername() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                // 从请求属性获取用户名（在JwtAuthenticationFilter中设置）
                String username = (String) request.getAttribute("X-User-Name");
                if (username != null && !username.isEmpty()) {
                    return username;
                }
            }
        } catch (Exception e) {
            log.warn("获取当前登录用户失败: {}", e.getMessage());
        }
        return "system";
    }
}
