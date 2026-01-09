package wake.su.zhuque.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wake.su.zhuque.common.security.util.PasswordUtil;
import wake.su.zhuque.dao.mapper.SysUserMapper;
import wake.su.zhuque.model.dto.PageResult;
import wake.su.zhuque.model.dto.UserQueryRequest;
import wake.su.zhuque.model.dto.UserUpdateRequest;
import wake.su.zhuque.model.entity.SysUserDO;
import wake.su.zhuque.service.SysUserService;

/**
 * 系统用户服务实现
 */
@Slf4j
@Service
public class SysUserServiceImpl implements SysUserService {

    @Resource
    private SysUserMapper sysUserMapper;

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
    public PageResult<SysUserDO> page(UserQueryRequest request) {
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

        return PageResult.of(result);
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

        return sysUserMapper.updateById(user) > 0;
    }

    @Override
    public boolean updateStatus(Long userId, Integer status) {
        log.info("更新用户状态: userId={}, status={}", userId, status);

        SysUserDO user = new SysUserDO();
        user.setId(userId);
        user.setStatus(status);

        return sysUserMapper.updateById(user) > 0;
    }
}
