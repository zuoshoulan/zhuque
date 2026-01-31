package wake.su.zhuque.common.security.util;

import java.util.Scanner;

/**
 * 密码生成工具 - 用于生成今日密码并显示明文和哈希值
 *
 * <p>使用方法：
 * <pre>
 * # 方式1：直接运行 main 方法
 * java wake.su.zhuque.common.security.util.PasswordGeneratorTool
 *
 * # 方式2：使用 Maven 运行
 * mvn exec:java -Dexec.mainClass="wake.su.zhuque.common.security.util.PasswordGeneratorTool" \
 * -Dexec.args="13800138000"
 *
 * # 方式3：编译后运行
 * javac -cp target/classes:$(mvn dependency:build-classpath -DincludeScope=compile) \
 * src/main/java/wake/su/zhuque/common/security/util/PasswordGeneratorTool.java
 * </pre>
 *
 * @author wake.su
 * @since 2025-01-29
 */
public class PasswordGeneratorTool {

  public static void main(String[] args) {
    System.out.println("========================================");
    System.out.println("       密码生成工具 - 朱雀广告平台");
    System.out.println("========================================");
    System.out.println();

    String phone;

    // 从命令行参数或交互式输入获取手机号
    if (args.length > 0) {
      phone = args[0];
      System.out.println("使用命令行参数手机号: " + phone);
    } else {
      Scanner scanner = new Scanner(System.in);
      try {
        System.out.print("请输入手机号: ");
        phone = scanner.nextLine();
      } finally {
        scanner.close();
      }
    }

    System.out.println();
    System.out.println("----------------------------------------");

    // 生成明文密码
    String plainPassword = PasswordGenerator.generate(phone);
    System.out.println("【明文密码】");
    System.out.println("  " + plainPassword);
    System.out.println();

    // 生成哈希值
    String hashPassword = PasswordGenerator.generateHash(phone);
    System.out.println("【哈希值】");
    System.out.println("  " + hashPassword);
    System.out.println();

    // 生成 SQL 更新语句
    System.out.println("----------------------------------------");
    System.out.println("【SQL 更新语句】");
    System.out.println("-- 方式1：根据手机号更新（推荐）");
    System.out.println(
        String.format(
            "UPDATE sys_user SET password = '%s' WHERE phone = '%s';",
            hashPassword, phone));

    System.out.println();
    System.out.println("-- 方式2：根据用户ID更新");
    System.out.println("-- 请将下面的 1 替换为实际的用户ID");
    System.out.println(
        String.format(
            "UPDATE sys_user SET password = '%s' WHERE id = 1;",
            hashPassword));

    System.out.println();
    System.out.println("========================================");
    System.out.println("【密码验证】");
    System.out.println("  明文密码: " + plainPassword);
    System.out.println("  哈希值:   " + hashPassword);
    System.out.println("  验证结果: " + (PasswordUtil.matches(plainPassword, hashPassword) ? "✅ 匹配" : "❌ 不匹配"));
    System.out.println("========================================");
  }

  /**
   * 快速生成密码（供其他程序调用）
   *
   * @param phone
   *          手机号
   * @return [明文密码, 哈希值]
   */
  public static String[] generatePasswordAndHash(String phone) {
    String plain = PasswordGenerator.generate(phone);
    String hash = PasswordGenerator.generateHash(phone);
    return new String [] { plain, hash };
  }

  /**
   * 打印密码信息（供其他程序调用）
   *
   * @param phone
   *          手机号
   */
  public static void printPasswordInfo(String phone) {
    String plain = PasswordGenerator.generate(phone);
    String hash = PasswordGenerator.generateHash(phone);

    System.out.println("手机号: " + phone);
    System.out.println("明文密码: " + plain);
    System.out.println("哈希值: " + hash);
    System.out.println(
        "SQL: UPDATE sys_user SET password = '" + hash + "' WHERE username = '" + phone + "';");
  }
}
