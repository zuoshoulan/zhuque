package wake.su.zhuque.common.security.annotation;

import java.lang.annotation.*;

/**
 * API接口权限校验注解
 * 用于第三层权限控制：接口级别的权限校验
 *
 * 使用场景：
 * 需要比按钮权限更细粒度的控制时使用
 *
 * 示例：
 * @RequiresApiPermission("api:advertiser:create")
 * public Result createAdvertiser() { ... }
 *
 * @author wake.su
 * @since 2026-01-11
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresApiPermission {

    /**
     * 需要的API权限编码
     */
    String[] value() default {};

    /**
     * 逻辑类型：AND/OR
     * AND: 需要拥有所有权限
     * OR: 拥有任一权限即可（默认）
     */
    LogicalType logical() default LogicalType.OR;

    /**
     * 逻辑类型枚举
     */
    enum LogicalType {
        AND,
        OR
    }
}
