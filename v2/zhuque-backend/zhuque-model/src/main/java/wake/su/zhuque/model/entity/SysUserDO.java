package wake.su.zhuque.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import wake.su.zhuque.model.enums.ThemePreferenceEnum;

import java.time.LocalDateTime;

/**
 * 系统用户实体
 */
@Data
@TableName("sys_user")
public class SysUserDO {

    /**
     * 用户ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 状态：0-禁用，1-正常
     */
    private Integer status;

    /**
     * 是否强制修改密码：0-否，1-是
     */
    private Integer forceChangePassword;

    /**
     * 主题偏好：0-亮色，1-暗色，2-自动
     */
    private ThemePreferenceEnum themePreference;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 删除标记：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
}
