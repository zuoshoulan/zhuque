package wake.su.zhuque.service;

import wake.su.zhuque.model.entity.SysUserDO;

/**
 * 系统用户服务接口
 */
public interface SysUserService {

    /**
     * 根据ID查询用户
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    SysUserDO getById(Long userId);

    /**
     * 根据账号查询用户
     *
     * @param account 账号
     * @return 用户信息
     */
    SysUserDO getByAccount(String account);

    /**
     * 保存用户
     *
     * @param user 用户信息
     * @return 是否成功
     */
    boolean save(SysUserDO user);

    /**
     * 更新用户
     *
     * @param user 用户信息
     * @return 是否成功
     */
    boolean updateById(SysUserDO user);

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean removeById(Long userId);
}
