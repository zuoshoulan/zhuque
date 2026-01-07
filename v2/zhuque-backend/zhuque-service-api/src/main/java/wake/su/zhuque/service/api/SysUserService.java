package wake.su.zhuque.service.api;

import wake.su.zhuque.model.entity.SysUser;

/**
 * 系统用户服务接口
 */
public interface SysUserService {

    /**
     * 根据用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    SysUser getById(Long userId);
}
