package wake.su.zhuque.common.security.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * PasswordGenerator 测试类 - 测试 generateHash 方法
 *
 * @author wake.su
 * @since 2025-01-29
 */
@DisplayName("密码哈希生成测试")
class PasswordGeneratorTest {

  // ==================== generateHash(String phone) 测试 ====================

  @Test
  @DisplayName("生成密码哈希 - 验证哈希格式")
  void testGenerateHashFormat() {
    // Given
    String phone = "13800138000";

    // When
    String hash = PasswordGenerator.generateHash(phone);

    // Then
    assertAll(
        "BCrypt哈希格式验证",
        () -> assertNotNull(hash, "哈希值不应为null"),
        () -> assertTrue(hash.length() >= 60, "BCrypt哈希长度应 >= 60"),
        () -> assertTrue(
            hash.startsWith("$2a$") || hash.startsWith("$2b$"),
            "BCrypt哈希应以 $2a$ 或 $2b$ 开头，实际: " + hash.substring(0, 4)));
  }

  @Test
  @DisplayName("生成密码哈希 - 验证可匹配性")
  void testGenerateHashMatches() {
    // Given
    String phone = "13800138000";
    String plainPassword = PasswordGenerator.generate(phone);

    // When
    String hash = PasswordGenerator.generateHash(phone);

    // Then
    assertTrue(
        PasswordUtil.matches(plainPassword, hash),
        "生成的哈希应能匹配原始密码");
  }

  @Test
  @DisplayName("生成密码哈希 - 验证同一手机号每次生成不同哈希（随机盐）")
  void testGenerateHashUniqueness() {
    // Given
    String phone = "13800138000";

    // When
    String hash1 = PasswordGenerator.generateHash(phone);
    String hash2 = PasswordGenerator.generateHash(phone);

    // Then
    assertAll(
        "BCrypt随机盐验证",
        () -> assertNotNull(hash1),
        () -> assertNotNull(hash2),
        () -> assertFalse(
            hash1.equals(hash2),
            "同一手机号两次BCrypt加密结果应不同（随机盐）\n" +
                "hash1: " + hash1 + "\n" +
                "hash2: " + hash2));
  }

  @Test
  @DisplayName("生成密码哈希 - 不同手机号生成不同哈希")
  void testGenerateHashDifferentPhones() {
    // Given
    String phone1 = "13800138000";
    String phone2 = "13912345678";

    // When
    String hash1 = PasswordGenerator.generateHash(phone1);
    String hash2 = PasswordGenerator.generateHash(phone2);

    // Then
    assertAll(
        "不同手机号应生成不同哈希",
        () -> assertNotNull(hash1),
        () -> assertNotNull(hash2),
        () -> assertFalse(
            hash1.equals(hash2),
            "不同手机号生成的哈希应不同\n" +
                "phone1 hash: " + hash1 + "\n" +
                "phone2 hash: " + hash2));
  }

  @Test
  @DisplayName("生成密码哈希 - 错误密码不应匹配")
  void testGenerateHashNotMatchesWrongPassword() {
    // Given
    String phone = "13800138000";
    String wrongPassword = "wrongpassword";

    // When
    String hash = PasswordGenerator.generateHash(phone);

    // Then
    assertFalse(
        PasswordUtil.matches(wrongPassword, hash),
        "错误密码不应匹配哈希值");
  }

  @Test
  @DisplayName("生成密码哈希 - 空手机号也应能生成哈希")
  void testGenerateHashWithEmptyPhone() {
    // Given
    String phone = "";

    // When
    String hash = PasswordGenerator.generateHash(phone);

    // Then
    assertAll(
        "空手机号哈希验证",
        () -> assertNotNull(hash),
        () -> assertTrue(hash.length() >= 60),
        () -> assertTrue(hash.startsWith("$2a$") || hash.startsWith("$2b$")));
  }

  @Test
  @DisplayName("生成密码哈希 - 特殊字符手机号")
  void testGenerateHashWithSpecialChars() {
    // Given - 包含特殊字符的手机号（实际可能不会出现，但测试健壮性）
    String phone = "+86-138-0013-8000";

    // When
    String hash = PasswordGenerator.generateHash(phone);
    String plainPassword = PasswordGenerator.generate(phone);

    // Then
    assertAll(
        "特殊字符手机号哈希验证",
        () -> assertNotNull(hash),
        () -> assertTrue(PasswordUtil.matches(plainPassword, hash)));
  }
}
