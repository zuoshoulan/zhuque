package wake.su.zhuque.admin.security.annotation;

import java.lang.annotation.*;

/**
 * 权限校验注解
 *
 * 使用示例： <pre> {@code @RequiresPermission("advertiser:create") @PostMapping("/api/advertisers") public Result
 * createAdvertiser(...) { // 业务逻辑 } } </pre>
 *
 * @author wake.su
 * @since 2026-01-09
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPermission {

  /**
   * 需要的权限编码（支持多个） 多个权限之间的关系为OR（满足其中一个即可）
   *
   * @return 权限编码数组
   */
  String[] value() default {};

  /**
   * 权限校验逻辑类型
   *
   * @return 逻辑类型
   */
  LogicalType logical() default LogicalType.OR;

  /**
   * 逻辑类型枚举
   */
  enum LogicalType {
    /**
     * 或关系：满足任一权限即可
     */
    OR,

    /**
     * 且关系：需要同时满足所有权限
     */
    AND
  }
}
