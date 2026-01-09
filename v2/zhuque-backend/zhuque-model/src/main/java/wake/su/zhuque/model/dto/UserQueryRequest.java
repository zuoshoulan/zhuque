package wake.su.zhuque.model.dto;

import lombok.Data;

/**
 * 用户查询请求
 */
@Data
public class UserQueryRequest {
    /**
     * 当前页
     */
    private Long current = 1L;

    /**
     * 每页大小
     */
    private Long size = 10L;

    /**
     * 关键词（用户名、昵称、手机号）
     */
    private String keyword;

    /**
     * 用户状态：0-禁用 1-启用
     */
    private Integer status;
}
