package wake.su.zhuque.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import wake.su.zhuque.dao.mapper.SysUserMapper;
import wake.su.zhuque.model.entity.SysUser;
import wake.su.zhuque.service.SysUserService;

/**
 * 系统用户服务实现
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
}
