package wake.su.zhuque.common.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

/**
 * JWT工具类 用于生成和验证JWT Token
 */
@Component
public class JwtUtil {

  /**
   * JWT密钥（建议从配置文件读取）
   */
  private static final String SECRET_KEY = "zhuque-platform-secret-key-for-jwt-token-generation-must-be-long-enough";

  /**
   * Token有效期（毫秒）默认7天
   */
  private static final long EXPIRATION = 7 * 24 * 60 * 60 * 1000;

  /**
   * 生成密钥
   */
  private SecretKey getSignKey() {
    return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * 生成Token
   *
   * @param subject
   *          主题（通常是用户ID或用户名）
   * @param claims
   *          自定义声明
   * @return Token字符串
   */
  public String generateToken(String subject, Map<String, Object> claims) {
    Date now = new Date();
    Date expiration = new Date(now.getTime() + EXPIRATION);

    JwtBuilder builder = Jwts.builder().subject(subject).issuedAt(now).expiration(expiration).signWith(getSignKey());

    if(claims != null && !claims.isEmpty()) {
      builder.addClaims(claims);
    }

    return builder.compact();
  }

  /**
   * 生成Token（无自定义声明）
   *
   * @param subject
   *          主题（通常是用户ID或用户名）
   * @return Token字符串
   */
  public String generateToken(String subject) {
    return generateToken(subject, null);
  }

  /**
   * 解析Token
   *
   * @param token
   *          Token字符串
   * @return Claims对象
   */
  public Claims parseToken(String token) {
    return Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(token).getPayload();
  }

  /**
   * 验证Token是否有效
   *
   * @param token
   *          Token字符串
   * @return 是否有效
   */
  public boolean validateToken(String token) {
    try {
      parseToken(token);
      return true;
    } catch(ExpiredJwtException e) {
      // Token已过期
      return false;
    } catch(JwtException e) {
      // Token无效
      return false;
    }
  }

  /**
   * 从Token中获取主题（用户ID或用户名）
   *
   * @param token
   *          Token字符串
   * @return 主题
   */
  public String getSubject(String token) {
    Claims claims = parseToken(token);
    return claims.getSubject();
  }

  /**
   * 从Token中获取用户ID
   *
   * @param token
   *          Token字符串
   * @return 用户ID
   */
  public Long getUserId(String token) {
    Claims claims = parseToken(token);
    String userId = claims.getSubject();
    return Long.valueOf(userId);
  }

  /**
   * 从Token中获取用户名
   *
   * @param token
   *          Token字符串
   * @return 用户名
   */
  public String getUsername(String token) {
    Claims claims = parseToken(token);
    return claims.get("username", String.class);
  }

  /**
   * 检查Token是否即将过期（剩余时间少于1小时）
   *
   * @param token
   *          Token字符串
   * @return 是否即将过期
   */
  public boolean isTokenExpiringSoon(String token) {
    Claims claims = parseToken(token);
    Date expiration = claims.getExpiration();
    Date now = new Date();
    long remainingTime = expiration.getTime() - now.getTime();
    return remainingTime < (60 * 60 * 1000); // 小于1小时
  }

  /**
   * 刷新Token
   *
   * @param token
   *          旧Token
   * @return 新Token
   */
  public String refreshToken(String token) {
    Claims claims = parseToken(token);
    String subject = claims.getSubject();
    return generateToken(subject);
  }
}
