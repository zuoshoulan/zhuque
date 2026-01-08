package wake.su.zhuque.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wake.su.zhuque.dao.mapper.SysUserMapper;
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
        log.debug("根据账号查询用户: account={}", account);
        return sysUserMapper.selectOne(
            new LambdaQueryWrapper<SysUserDO>()
                .eq(SysUserDO::getPhone, account)
        );
    }
}
