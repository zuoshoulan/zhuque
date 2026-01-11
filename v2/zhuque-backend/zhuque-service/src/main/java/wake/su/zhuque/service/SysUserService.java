package wake.su.zhuque.service;

import wake.su.zhuque.model.dto.PageResult;
import wake.su.zhuque.model.dto.ResetPasswordResponse;
import wake.su.zhuque.model.dto.UserQueryRequest;
import wake.su.zhuque.model.dto.UserUpdateRequest;
import wake.su.zhuque.model.entity.SysUserDO;
import wake.su.zhuque.model.vo.RoleVO;

import java.util.List;

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
     * 根据账号（手机号）查询用户
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

    /**
     * 分页查询用户
     *
     * @param request 查询请求
     * @return 分页结果
     */
    PageResult<SysUserDO> page(UserQueryRequest request);

    /**
     * 创建用户
     *
     * @param request 创建请求
     * @return 用户ID
     */
    Long createUser(UserUpdateRequest request);

    /**
     * 更新用户信息
     *
     * @param userId  用户ID
     * @param request 更新请求
     * @return 是否成功
     */
    boolean updateUser(Long userId, UserUpdateRequest request);

    /**
     * 更新用户状态
     *
     * @param userId 用户ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateStatus(Long userId, Integer status);

    /**
     * 更新用户主题偏好
     *
     * @param userId 用户ID
     * @param theme  主题偏好 (light/dark/auto)
     * @return 是否成功
     */
    boolean updateThemePreference(Long userId, String theme);

    /**
     * 重置用户密码
     *
     * @param userId     用户ID
     * @param newPassword 新密码（可选，为null则使用默认规则生成）
     * @return 重置密码响应（包含明文密码，仅此一次返回）
     */
    ResetPasswordResponse resetPassword(Long userId, String newPassword);

    /**
     * 获取用户的角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<RoleVO> getUserRoles(Long userId);

    /**
     * 为用户分配角色
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     * @return 是否成功
     */
    boolean assignRoles(Long userId, List<Long> roleIds);

    /**
     * 修改密码
     *
     * @param userId      用户ID
     * @param oldPassword 原密码
     * @param newPassword 新密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);
}
