package wake.su.zhuque.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import wake.su.zhuque.dao.mapper.SysUserMapper;
import wake.su.zhuque.model.entity.SysUser;
import wake.su.zhuque.service.api.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 系统用户服务实现
 */
@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public SysUser getById(Long userId) {
        return sysUserMapper.selectById(userId);
    }
}
