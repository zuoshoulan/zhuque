package wake.su.zhuque.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import wake.su.zhuque.common.config.SuperAdminConfig;
import wake.su.zhuque.common.core.exception.BusinessException;
import wake.su.zhuque.common.core.result.PageInfo;
import wake.su.zhuque.common.core.result.Result;
import wake.su.zhuque.common.security.SuperAdminHolder;
import wake.su.zhuque.common.security.util.PasswordGenerator;
import wake.su.zhuque.common.security.util.PasswordUtil;
import wake.su.zhuque.dao.mapper.SysRoleMapper;
import wake.su.zhuque.dao.mapper.SysUserMapper;
import wake.su.zhuque.dao.mapper.SysUserRoleMapper;
import wake.su.zhuque.model.dto.ResetPasswordResponse;
import wake.su.zhuque.model.dto.UserQueryRequest;
import wake.su.zhuque.model.dto.UserUpdateRequest;
import wake.su.zhuque.model.entity.SysRoleDO;
import wake.su.zhuque.model.entity.SysUserRoleDO;
import wake.su.zhuque.model.entity.SysUserDO;
import wake.su.zhuque.model.enums.ThemePreferenceEnum;
import wake.su.zhuque.model.vo.RoleVO;
import wake.su.zhuque.service.SysUserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统用户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SuperAdminConfig superAdminConfig;

    @Override
    public SysUserDO getById(Long userId) {
        log.debug("根据ID查询用户: userId={}", userId);
        return sysUserMapper.selectById(userId);
    }

    @Override
    public boolean save(SysUserDO user) {
        log.info("保存用户: username={}", user.getUsername());
        return sysUserMapper.insert(user) > 0;
    }

    @Override
    public boolean updateById(SysUserDO user) {
        log.info("更新用户: userId={}", user.getId());
        return sysUserMapper.updateById(user) > 0;
    }

    @Override
    public boolean removeById(Long userId) {
        log.info("删除用户: userId={}", userId);

        // 检查是否为超级管理员
        SuperAdminHolder.checkNotSuperAdmin(userId, superAdminConfig);

        return sysUserMapper.deleteById(userId) > 0;
    }

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    public boolean existsByUsername(String username) {
        Long count = sysUserMapper.selectCount(
            new LambdaQueryWrapper<SysUserDO>()
                .eq(SysUserDO::getUsername, username)
        );
        return count != null && count > 0;
    }

    @Override
    public SysUserDO getByAccount(String account) {
        log.debug("根据账号查询用户（第一版仅支持手机号）: account={}", account);
        // 第一版：account 仅支持手机号
        return sysUserMapper.selectOne(
            new LambdaQueryWrapper<SysUserDO>()
                .eq(SysUserDO::getPhone, account)
        );
        // TODO: 后续版本可能扩展支持邮箱、用户名等
        // .or().eq(SysUserDO::getEmail, account)
        // .or().eq(SysUserDO::getUsername, account)
    }

    @Override
    public Result<List<SysUserDO>> page(UserQueryRequest request) {
        log.debug("分页查询用户: request={}", request);

        // 构建查询条件
        LambdaQueryWrapper<SysUserDO> queryWrapper = new LambdaQueryWrapper<>();

        // 关键词搜索
        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                .like(SysUserDO::getUsername, request.getKeyword())
                .or()
                .like(SysUserDO::getNickname, request.getKeyword())
                .or()
                .like(SysUserDO::getPhone, request.getKeyword())
            );
        }

        // 状态筛选
        if (request.getStatus() != null) {
            queryWrapper.eq(SysUserDO::getStatus, request.getStatus());
        }

        // 分页查询
        Page<SysUserDO> page = new Page<>(request.getCurrent(), request.getSize());
        Page<SysUserDO> result = sysUserMapper.selectPage(page, queryWrapper);

        return Result.success(result.getRecords(), PageInfo.of(request.getCurrent(), request.getSize(), result.getTotal()));
    }

    @Override
    public Long createUser(UserUpdateRequest request) {
        log.info("创建用户: username={}", request.getUsername());

        // 检查用户名是否已存在
        Long count = sysUserMapper.selectCount(
            new LambdaQueryWrapper<SysUserDO>()
                .eq(SysUserDO::getUsername, request.getUsername())
        );
        if (count != null && count > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查手机号是否已存在
        if (request.getPhone() != null) {
            count = sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUserDO>()
                    .eq(SysUserDO::getPhone, request.getPhone())
            );
            if (count != null && count > 0) {
                throw new RuntimeException("手机号已存在");
            }
        }

        // 创建用户实体
        SysUserDO user = new SysUserDO();
        user.setUsername(request.getUsername());
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setPassword(PasswordUtil.encode(request.getPassword()));
        user.setStatus(request.getStatus() != null ? request.getStatus() : 1);

        // 设置创建人和创建时间
        String currentUsername = getCurrentUsername();
        user.setCreateBy(currentUsername);
        user.setUpdateBy(currentUsername);
        LocalDateTime now = LocalDateTime.now();
        user.setCreateTime(now);
        user.setUpdateTime(now);

        sysUserMapper.insert(user);

        return user.getId();
    }

    @Override
    public boolean updateUser(Long userId, UserUpdateRequest request) {
        log.info("更新用户: userId={}", userId);

        SysUserDO user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 更新字段
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        // 设置更新人和更新时间
        String currentUsername = getCurrentUsername();
        user.setUpdateBy(currentUsername);
        user.setUpdateTime(LocalDateTime.now());

        return sysUserMapper.updateById(user) > 0;
    }

    @Override
    public boolean updateStatus(Long userId, Integer status) {
        log.info("更新用户状态: userId={}, status={}", userId, status);

        // 禁止禁用超级管理员
        if (status == 0) {
            SuperAdminHolder.checkNotSuperAdmin(userId, superAdminConfig);
        }

        SysUserDO user = new SysUserDO();
        user.setId(userId);
        user.setStatus(status);
        user.setUpdateBy(getCurrentUsername());  // 必须显式设置更新人
        user.setUpdateTime(LocalDateTime.now());  // 必须显式设置更新时间

        return sysUserMapper.updateById(user) > 0;
    }

    @Override
    public boolean updateThemePreference(Long userId, String theme) {
        log.info("更新用户主题偏好: userId={}, theme={}", userId, theme);

        // 将字符串转换为枚举
        ThemePreferenceEnum themeEnum = ThemePreferenceEnum.fromValue(theme);

        SysUserDO user = new SysUserDO();
        user.setId(userId);
        user.setThemePreference(themeEnum);
        user.setUpdateBy(getCurrentUsername());  // 必须显式设置更新人
        user.setUpdateTime(LocalDateTime.now());  // 必须显式设置更新时间

        return sysUserMapper.updateById(user) > 0;
    }

    @Override
    public ResetPasswordResponse resetPassword(Long userId, String newPassword) {
        log.info("重置用户密码: userId={}, customPassword={}", userId, newPassword != null);

        // 查询用户
        SysUserDO user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 生成密码
        String finalPassword;
        String passwordType;
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            // 使用自定义密码
            finalPassword = newPassword;
            passwordType = "custom";
            log.info("使用自定义密码重置: userId={}", userId);
        } else {
            // 使用默认规则生成
            if (user.getPhone() == null || user.getPhone().trim().isEmpty()) {
                throw new RuntimeException("用户手机号为空，无法生成默认密码");
            }
            finalPassword = PasswordGenerator.generate(user.getPhone());
            passwordType = "default";
            log.info("使用默认规则生成密码: userId={}, phone={}", userId, user.getPhone());
        }

        // 加密密码
        String hashedPassword = PasswordUtil.encode(finalPassword);

        // 更新数据库
        SysUserDO updateUser = new SysUserDO();
        updateUser.setId(userId);
        updateUser.setPassword(hashedPassword);
        updateUser.setForceChangePassword(1); // 强制用户下次登录时修改密码
        updateUser.setUpdateBy(getCurrentUsername());  // 必须显式设置更新人
        updateUser.setUpdateTime(LocalDateTime.now());  // 必须显式设置更新时间
        boolean success = sysUserMapper.updateById(updateUser) > 0;

        if (!success) {
            throw new RuntimeException("密码重置失败");
        }

        // 构建响应（返回明文密码，仅此一次）
        return ResetPasswordResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .phone(user.getPhone())
                .password(finalPassword)  // 明文密码，仅此一次返回
                .passwordType(passwordType)
                .build();
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

    @Override
    public List<RoleVO> getUserRoles(Long userId) {
        // 查询用户的角色ID列表
        List<SysUserRoleDO> userRoles = sysUserRoleMapper.selectList(
            new LambdaQueryWrapper<SysUserRoleDO>()
                .eq(SysUserRoleDO::getUserId, userId)
        );

        if (userRoles.isEmpty()) {
            return List.of();
        }

        // 获取角色ID列表
        List<Long> roleIds = userRoles.stream()
            .map(SysUserRoleDO::getRoleId)
            .collect(Collectors.toList());

        // 查询角色详情
        List<SysRoleDO> roles = sysRoleMapper.selectBatchIds(roleIds);

        // 转换为VO
        return roles.stream()
            .filter(role -> role.getStatus() == 1) // 只返回启用的角色
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
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRoles(Long userId, List<Long> roleIds) {
        // 禁止为超级管理员分配角色
        if (SuperAdminHolder.isSuperAdmin(userId, superAdminConfig)) {
            throw new BusinessException("超级管理员自动拥有所有权限，无需分配角色");
        }

        // 验证用户是否存在
        SysUserDO user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 删除用户的所有角色
        sysUserRoleMapper.delete(
            new LambdaQueryWrapper<SysUserRoleDO>()
                .eq(SysUserRoleDO::getUserId, userId)
        );

        // 分配新角色
        if (roleIds != null && !roleIds.isEmpty()) {
            // 验证角色是否存在
            List<SysRoleDO> roles = sysRoleMapper.selectBatchIds(roleIds);
            if (roles.size() != roleIds.size()) {
                throw new RuntimeException("部分角色不存在");
            }

            // 插入用户角色关联
            String currentUser = getCurrentUsername();
            LocalDateTime now = LocalDateTime.now();
            List<SysUserRoleDO> userRoleList = roleIds.stream()
                .map(roleId -> {
                    SysUserRoleDO userRole = new SysUserRoleDO();
                    userRole.setUserId(userId);
                    userRole.setRoleId(roleId);
                    userRole.setCreateTime(now);  // 必须显式设置创建时间
                    userRole.setCreateBy(currentUser);  // 必须显式设置创建人
                    return userRole;
                })
                .collect(Collectors.toList());

            userRoleList.forEach(sysUserRoleMapper::insert);
        }

        log.info("为用户分配角色成功: userId={}, roleIds={}, operator={}", userId, roleIds, getCurrentUsername());
        return true;
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        log.info("修改密码: userId={}", userId);

        // 查询用户
        SysUserDO user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证原密码
        if (!PasswordUtil.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        // 加密新密码
        String hashedPassword = PasswordUtil.encode(newPassword);

        // 更新密码
        SysUserDO updateUser = new SysUserDO();
        updateUser.setId(userId);
        updateUser.setPassword(hashedPassword);
        updateUser.setForceChangePassword(0); // 清除强制修改密码标记
        updateUser.setUpdateBy(getCurrentUsername());
        updateUser.setUpdateTime(LocalDateTime.now());

        boolean success = sysUserMapper.updateById(updateUser) > 0;
        if (!success) {
            throw new RuntimeException("密码修改失败");
        }

        log.info("密码修改成功: userId={}", userId);
    }
}
