package wake.su.zhuque.service.impl.permission;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wake.su.zhuque.dao.mapper.SysPermissionMapper;
import wake.su.zhuque.dao.mapper.SysRoleMapper;
import wake.su.zhuque.dao.mapper.SysRolePermissionMapper;
import wake.su.zhuque.dao.mapper.SysUserRoleMapper;
import wake.su.zhuque.model.dto.AssignPermissionsRequest;
import wake.su.zhuque.model.dto.PageResult;
import wake.su.zhuque.model.dto.RoleCreateRequest;
import wake.su.zhuque.model.entity.SysPermissionDO;
import wake.su.zhuque.model.entity.SysRoleDO;
import wake.su.zhuque.model.entity.SysRolePermissionDO;
import wake.su.zhuque.model.entity.SysUserRoleDO;
import wake.su.zhuque.model.query.RolePageQuery;
import wake.su.zhuque.model.vo.PermissionVO;
import wake.su.zhuque.model.vo.RoleVO;
import wake.su.zhuque.service.api.RoleService;
import wake.su.zhuque.common.config.SuperAdminConfig;
import wake.su.zhuque.common.core.exception.BusinessException;
import wake.su.zhuque.common.security.SuperAdminHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色管理服务实现
 *
 * @author wake.su
 * @since 2026-01-09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final SysRoleMapper roleMapper;
    private final SysPermissionMapper permissionMapper;
    private final SysRolePermissionMapper rolePermissionMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SuperAdminConfig superAdminConfig;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRole(RoleCreateRequest request) {
        // 检查角色编码是否已存在
        SysRoleDO existing = roleMapper.selectOne(
            new LambdaQueryWrapper<SysRoleDO>()
                .eq(SysRoleDO::getRoleCode, request.getRoleCode())
        );
        if (existing != null) {
            throw new RuntimeException("角色编码已存在：" + request.getRoleCode());
        }

        SysRoleDO role = new SysRoleDO();
        role.setRoleCode(request.getRoleCode());
        role.setRoleName(request.getRoleName());
        role.setDescription(request.getDescription());
        role.setStatus(1);

        roleMapper.insert(role);
        log.info("创建角色成功：{}", role.getRoleCode());

        // 如果有权限，分配权限
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            AssignPermissionsRequest assignRequest = new AssignPermissionsRequest();
            assignRequest.setRoleId(role.getId());
            assignRequest.setPermissionIds(request.getPermissionIds());
            assignPermissions(assignRequest);
        }

        return role.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(Long id, RoleCreateRequest request) {
        SysRoleDO role = roleMapper.selectById(id);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }

        role.setRoleName(request.getRoleName());
        role.setDescription(request.getDescription());
        role.setUpdateBy(getCurrentUsername());  // 必须显式设置更新人
        role.setUpdateTime(LocalDateTime.now());  // 必须显式设置更新时间
        roleMapper.updateById(role);

        log.info("更新角色成功：{}", role.getRoleCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        // 检查是否有超级管理员关联该角色
        if (superAdminConfig.isEnabled()) {
            List<Long> superAdminIds = superAdminConfig.getUserIds();
            if (superAdminIds != null && !superAdminIds.isEmpty()) {
                // 查询超级管理员是否拥有该角色
                Long count = userRoleMapper.selectCount(
                    new LambdaQueryWrapper<SysUserRoleDO>()
                        .in(SysUserRoleDO::getUserId, superAdminIds)
                        .eq(SysUserRoleDO::getRoleId, id)
                );
                if (count != null && count > 0) {
                    throw new BusinessException("该角色已分配给超级管理员，禁止删除");
                }
            }
        }

        // 检查是否有用户关联该角色
        // TODO: 实现用户角色关联检查

        // 删除角色的权限关联
        rolePermissionMapper.delete(
            new LambdaQueryWrapper<SysRolePermissionDO>()
                .eq(SysRolePermissionDO::getRoleId, id)
        );

        // 删除角色
        roleMapper.deleteById(id);
        log.info("删除角色成功：{}", id);
    }

    @Override
    public RoleVO getRole(Long id) {
        SysRoleDO role = roleMapper.selectById(id);
        if (role == null) {
            return null;
        }

        RoleVO vo = new RoleVO();
        vo.setId(role.getId());
        vo.setRoleCode(role.getRoleCode());
        vo.setRoleName(role.getRoleName());
        vo.setDescription(role.getDescription());
        vo.setStatus(role.getStatus());
        vo.setCreateTime(role.getCreateTime());

        // 查询角色的权限ID列表
        List<Long> permissionIds = getRolePermissionIds(id);
        vo.setPermissionIds(permissionIds);

        return vo;
    }

    @Override
    public List<RoleVO> getRoleList() {
        List<SysRoleDO> roles = roleMapper.selectList(
            new LambdaQueryWrapper<SysRoleDO>()
                .eq(SysRoleDO::getStatus, 1)
        );

        return roles.stream()
            .map(role -> {
                RoleVO vo = new RoleVO();
                vo.setId(role.getId());
                vo.setRoleCode(role.getRoleCode());
                vo.setRoleName(role.getRoleName());
                vo.setDescription(role.getDescription());
                vo.setStatus(role.getStatus());
                vo.setCreateTime(role.getCreateTime());
                return vo;
            })
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(AssignPermissionsRequest request) {
        // 删除角色的所有权限
        rolePermissionMapper.delete(
            new LambdaQueryWrapper<SysRolePermissionDO>()
                .eq(SysRolePermissionDO::getRoleId, request.getRoleId())
        );

        // 分配新权限
        if (!request.getPermissionIds().isEmpty()) {
            List<SysRolePermissionDO> list = request.getPermissionIds().stream()
                .map(permissionId -> {
                    SysRolePermissionDO rp = new SysRolePermissionDO();
                    rp.setRoleId(request.getRoleId());
                    rp.setPermissionId(permissionId);
                    return rp;
                })
                .collect(Collectors.toList());

            list.forEach(rolePermissionMapper::insert);
        }

        log.info("为角色{}分配权限成功，共{}个", request.getRoleId(), request.getPermissionIds().size());

        // TODO: 清除Redis缓存
    }

    @Override
    public List<Long> getRolePermissionIds(Long roleId) {
        List<SysRolePermissionDO> list = rolePermissionMapper.selectList(
            new LambdaQueryWrapper<SysRolePermissionDO>()
                .eq(SysRolePermissionDO::getRoleId, roleId)
        );

        return list.stream()
            .map(SysRolePermissionDO::getPermissionId)
            .collect(Collectors.toList());
    }

    @Override
    public List<PermissionVO> getRolePermissions(Long roleId) {
        List<Long> permissionIds = getRolePermissionIds(roleId);

        if (permissionIds.isEmpty()) {
            return List.of();
        }

        List<SysPermissionDO> permissions = permissionMapper.selectList(
            new LambdaQueryWrapper<SysPermissionDO>()
                .in(SysPermissionDO::getId, permissionIds)
                .eq(SysPermissionDO::getStatus, 1)
        );

        return permissions.stream()
            .map(p -> {
                PermissionVO vo = new PermissionVO();
                vo.setId(p.getId());
                vo.setParentId(p.getParentId());
                vo.setPermissionCode(p.getPermissionCode());
                vo.setPermissionName(p.getPermissionName());
                vo.setPermissionType(p.getPermissionType());
                vo.setPath(p.getPath());
                vo.setMethod(p.getMethod());
                vo.setIcon(p.getIcon());
                vo.setSortOrder(p.getSortOrder());
                vo.setStatus(p.getStatus());
                vo.setCreateTime(p.getCreateTime());
                return vo;
            })
            .collect(Collectors.toList());
    }

    @Override
    public PageResult<RoleVO> getRolePage(RolePageQuery query) {
        // 构建查询条件
        LambdaQueryWrapper<SysRoleDO> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索
        if (StringUtils.isNotBlank(query.getKeyword())) {
            wrapper.and(w -> w
                .like(SysRoleDO::getRoleName, query.getKeyword())
                .or()
                .like(SysRoleDO::getRoleCode, query.getKeyword())
            );
        }

        // 状态筛选
        if (query.getStatus() != null) {
            wrapper.eq(SysRoleDO::getStatus, query.getStatus());
        }

        // 按创建时间倒序
        wrapper.orderByDesc(SysRoleDO::getCreateTime);

        // 分页查询
        Page<SysRoleDO> page = new Page<>(
            query.getCurrent() != null ? query.getCurrent() : 1,
            query.getSize() != null ? query.getSize() : 10
        );

        Page<SysRoleDO> result = roleMapper.selectPage(page, wrapper);

        // 转换为VO
        List<RoleVO> vos = result.getRecords().stream()
            .map(role -> {
                RoleVO vo = new RoleVO();
                vo.setId(role.getId());
                vo.setRoleCode(role.getRoleCode());
                vo.setRoleName(role.getRoleName());
                vo.setDescription(role.getDescription());
                vo.setStatus(role.getStatus());
                vo.setCreateTime(role.getCreateTime());
                vo.setUpdateTime(role.getUpdateTime());
                return vo;
            })
            .collect(Collectors.toList());

        return PageResult.of(vos, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRoleStatus(Long id, Integer status) {
        SysRoleDO role = new SysRoleDO();
        role.setId(id);
        role.setStatus(status);
        role.setUpdateBy(getCurrentUsername());  // 必须显式设置更新人
        role.setUpdateTime(LocalDateTime.now());  // 必须显式设置更新时间
        roleMapper.updateById(role);
        log.info("修改角色状态成功：roleId={}, status={}", id, status);
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
