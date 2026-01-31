package wake.su.zhuque.common.security.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码工具类 <p> 使用BCrypt算法进行密码加密和验证 BCrypt特点： 1. 每次加密结果都不同（自动加盐） 2. 计算密集型，抵御暴力破解 3. 可调整加密强度 </p>
 */
public class PasswordUtil {

  private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

  /**
   * 加密密码 <p> 每次调用都会生成不同的加密结果，但验证时可以正确匹配 </p>
   *
   * @param rawPassword
   *          原始密码
   * @return 加密后的密码
   */
  public static String encode(String rawPassword) {
    return PASSWORD_ENCODER.encode(rawPassword);
  }

  /**
   * 验证密码
   *
   * @param rawPassword
   *          原始密码
   * @param encodedPassword
   *          加密后的密码
   * @return true-密码匹配，false-密码不匹配
   */
  public static boolean matches(String rawPassword, String encodedPassword) {
    return PASSWORD_ENCODER.matches(rawPassword, encodedPassword);
  }
}
