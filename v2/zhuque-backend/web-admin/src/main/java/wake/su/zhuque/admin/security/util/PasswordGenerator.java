package wake.su.zhuque.admin.security.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 密码生成工具类
 * <p>
 * 规则：yyyyMMdd + 手机号
 * </p>
 * <ul>
 *   <li>13800138001 → 2026010913800138001</li>
 *   <li>13912345678 → 2026010913912345678</li>
 * </ul>
 *
 * @author wake.su
 * @since 2026-01-09
 */
@Slf4j
public class PasswordGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 生成今日密码
     *
     * @param phone 手机号
     * @return 密码（yyyyMMdd + 手机号）
     */
    public static String generate(String phone) {
        String today = LocalDate.now().format(DATE_FORMATTER);
        String password = today + phone;
        log.debug("生成密码: phone={}, password={}", phone, password);
        return password;
    }

    /**
     * 生成指定日期的密码
     *
     * @param phone 手机号
     * @param date  日期
     * @return 密码（yyyyMMdd + 手机号）
     */
    public static String generate(String phone, LocalDate date) {
        String dateStr = date.format(DATE_FORMATTER);
        String password = dateStr + phone;
        log.debug("生成密码: phone={}, date={}, password={}", phone, dateStr, password);
        return password;
    }

    /**
     * 生成密码的哈希值（用于数据库存储）
     *
     * @param phone 手机号
     * @return BCrypt 哈希值
     */
    public static String generateHash(String phone) {
        String password = generate(phone);
        return PasswordUtil.encode(password);
    }
}
