# Java 代码格式化规范

本文档说明项目中使用的 Java 代码格式化标准，确保团队代码风格统一。

## 概述

| 项目 | 配置 |
|------|------|
| 格式化风格 | Google Java Style |
| 缩进 | 2空格 |
| 行宽限制 | 120 |
| 格式化注释 | 否 |

### 行宽限制说明

**120 字符** 是当前业界最通用的标准，被以下主流规范采用：

- **Alibaba Java 开发手册** - 120 字符（强制）
- **IntelliJ IDEA** - 默认 120 字符
- **Spring Framework** - 120 字符上限
- **Google Java Style** - 100 字符（略保守）

超过 120 字符的代码行会被格式化器自动换行，确保链式调用、Stream 操作等保持良好的可读性。

## VSCode 配置

### 安装推荐插件

打开项目后，VSCode 会提示安装以下插件：
- Red Hat Java
- Visual Studio Code Java Extension Pack

### 用户级别配置（所有项目生效）

在 `~/.vscode/settings.json` 中添加：

```json
{
  "java.format.enabled": true,
  "java.format.settings.profile": "GoogleStyle",
  "java.format.comments.enabled": false,
  "[java]": {
    "editor.formatOnSave": true,
    "editor.defaultFormatter": "redhat.java"
  }
}
```

> **注意**：不要配置 `java.format.settings.url`，让 VSCode 使用项目本地的 `.settings/org.eclipse.jdt.core.prefs` 配置文件。

### 快捷键

- **格式化当前文件**：`Shift + Alt + F`
- **保存时自动格式化**：已启用

## Maven 项目配置

### pom.xml 配置

在项目的 `pom.xml` 中添加 Spotless 插件：

```xml
<build>
  <plugins>
    <plugin>
      <groupId>com.diffplug.spotless</groupId>
      <artifactId>spotless-maven-plugin</artifactId>
      <version>2.43.0</version>
      <configuration>
        <java>
          <eclipse>
            <version>4.26</version>
            <file>${basedir}/.settings/org.eclipse.jdt.core.prefs</file>
          </eclipse>
          <removeUnusedImports />
          <importOrder>
            <order>java|javax,org,com,项目包名,</order>
          </importOrder>
        </java>
      </configuration>
      <executions>
        <execution>
          <id>spotless-check</id>
          <phase>validate</phase>
          <goals>
            <goal>check</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

### Eclipse 格式化配置文件

创建 `.settings/org.eclipse.jdt.core.prefs` 文件（放在项目根目录或 `src` 目录下），配置如下：

```properties
eclipse.preferences.version=1
org.eclipse.jdt.core.formatter.lineSplit=120
org.eclipse.jdt.core.formatter.comment.line_length=120
org.eclipse.jdt.core.formatter.comment.format_block_comments=false
org.eclipse.jdt.core.formatter.comment.format_comments=false
org.eclipse.jdt.core.formatter.comment.format_html=false
org.eclipse.jdt.core.formatter.comment.format_javadoc=false
org.eclipse.jdt.core.formatter.comment.format_line_comment=false
org.eclipse.jdt.core.formatter.tabulation.char=space
org.eclipse.jdt.core.formatter.tabulation.size=2
org.eclipse.jdt.core.formatter.indentation.size=2
org.eclipse.jdt.core.formatter.brace_position_for_block=end_of_line
org.eclipse.jdt.core.formatter.brace_position_for_constructor_declaration=end_of_line
org.eclipse.jdt.core.formatter.brace_position_for_method_declaration=end_of_line
org.eclipse.jdt.core.formatter.brace_position_for_type_declaration=end_of_line
org.eclipse.jdt.core.formatter.brace_position_for_anonymous_type_declaration=end_of_line
org.eclipse.jdt.core.formatter.brace_position_for_enum_declaration=end_of_line
org.eclipse.jdt.core.formatter.brace_position_for_lambda_body=end_of_line
org.eclipse.jdt.core.formatter.brace_position_for_switch_case=end_of_line
org.eclipse.jdt.core.formatter.compact_else_if=true
org.eclipse.jdt.core.formatter.continuation_indentation=2
org.eclipse.jdt.core.formatter.continuation_indentation_for_array_initializer=2
org.eclipse.jdt.core.formatter.format_guardian_clause_on_one_line=false
org.eclipse.jdt.core.formatter.indent_body_declarations_compare_to_type_header=true
org.eclipse.jdt.core.formatter.indent_breaks_compare_to_contents=true
org.eclipse.jdt.core.formatter.indent_empty_lines=false
org.eclipse.jdt.core.formatter.indent_statements_compare_to_body=true
org.eclipse.jdt.core.formatter.indent_switchstatements_compare_to_cases=true
org.eclipse.jdt.core.formatter.indent_switchstatements_compare_to_switch=false
org.eclipse.jdt.core.formatter.insert_new_line_in_empty_annotation_declaration=insert
org.eclipse.jdt.core.formatter.insert_new_line_in_empty_anonymous_type_declaration=insert
org.eclipse.jdt.core.formatter.insert_new_line_in_empty_block=insert
org.eclipse.jdt.core.formatter.insert_new_line_in_empty_enum_constant=insert
org.eclipse.jdt.core.formatter.insert_new_line_in_empty_enum_declaration=insert
org.eclipse.jdt.core.formatter.insert_new_line_in_empty_method_body=insert
org.eclipse.jdt.core.formatter.insert_new_line_in_empty_type_declaration=insert
org.eclipse.jdt.core.formatter.join_lines_in_comments=false
org.eclipse.jdt.core.formatter.join_wrapped_lines=false
org.eclipse.jdt.core.formatter.keep_else_statement_on_same_line=false
org.eclipse.jdt.core.formatter.keep_empty_array_initializer_on_one_line=false
org.eclipse.jdt.core.formatter.keep_imple_if_on_one_line=false
org.eclipse.jdt.core.formatter.keep_then_statement_on_same_line=false
org.eclipse.jdt.core.formatter.never_indent_block_comments_on_first_column=false
org.eclipse.jdt.core.formatter.never_indent_line_comments_on_first_column=false
org.eclipse.jdt.core.formatter.number_of_blank_lines_at_beginning_of_method_body=0
org.eclipse.jdt.core.formatter.number_of_empty_lines_to_preserve=1
org.eclipse.jdt.core.formatter.put_empty_statement_on_new_line=true
org.eclipse.jdt.core.formatter.wrap_before_assignment_operator=false
org.eclipse.jdt.core.formatter.wrap_before_binary_operator=true
org.eclipse.jdt.core.formatter.wrap_before_conditional_operator=true
org.eclipse.jdt.core.formatter.wrap_outer_expressions_when_nested=true
```

## Maven 命令

```bash
# 检查格式（不修改文件）
mvn spotless:check

# 自动格式化代码
mvn spotless:apply

# 查看哪些文件会被修改（不实际修改）
mvn spotless:diff

# 跳过格式化检查（仅紧急情况）
mvn install -Dspotless.skip
```

## 验证格式化不改变功能

在应用格式化到现有项目时，建议验证格式化前后编译产物一致：

### 字节码对比（推荐）

```bash
# 1. 格式化前编译并保存哈希
mvn clean compile -DskipTests
find target/classes -type f -name "*.class" | sort | xargs sha256sum > before.sum

# 2. 应用格式化
mvn spotless:apply

# 3. 格式化后编译并保存哈希
mvn clean compile -DskipTests
find target/classes -type f -name "*.class" | sort | xargs sha256sum > after.sum

# 4. 对比哈希值（应该完全相同）
diff before.sum after.sum
```

如果输出为空，说明格式化前后字节码完全一致，功能不受影响。

### 结合测试验证

```bash
# 创建格式化分支
git checkout -b format-code

# 运行测试确保原代码正常
mvn clean test

# 应用格式化
mvn spotless:apply

# 再次运行测试
mvn clean test

# 查看改动（应该只有空格、换行等格式变化）
git diff --stat
```

## 在新电脑上设置

### 1. 安装必要软件

- JDK 17+
- Maven 3.6+
- VSCode

### 2. 配置 VSCode 用户设置

打开 VSCode 设置（`Ctrl + ,`），点击右上角 `{}` 图标编辑用户级 `settings.json`，添加上面的配置。

### 3. 克隆项目后

```bash
cd zhuque-backend/bid-java
mvn spotless:apply
```

### 4. 验证配置

```bash
mvn spotless:check
```

应该显示 `BUILD SUCCESS`。

## 代码风格要点

- **缩进**：2空格
- **大括号**：K&R 风格（左大括号不换行）
- **注释**：不自动格式化，保留手动排版

### 多行文本示例

```java
// 推荐：使用文本块
System.out.println("""
    ========================================
       朱雀竞价服务启动成功！
       竞价接口: http://localhost:%s/openrtb/bid
    ========================================
    """.formatted(port));

// 避免：字符串拼接（会被格式化器合并）
System.out.println("第一行\n" + "第二行\n");
```

## 常见问题

### Q: VSCode 格式化和 Maven 命令不一致？

A: 确保 `~/.vscode/settings.json` 配置正确，然后重新加载窗口（`Ctrl+Shift+P` → `Developer: Reload Window`）。

### Q: 提交代码时格式检查失败？

A: 运行 `mvn spotless:apply` 后重新提交。

### Q: 想跳过格式化检查？

A: 临时跳过：`mvn install -Dspotless.skip`（不推荐）

### Q: Javadoc 多行注释被合并成一行？

A: 这是默认行为。如果想让 Javadoc 保留多行格式，确保配置文件中：
```properties
org.eclipse.jdt.core.formatter.join_lines_in_comments=false
org.eclipse.jdt.core.formatter.join_wrapped_lines=false
```

正确示例：
```java
/**
 * 投放活动表 Mapper
 * 三层架构：Campaign（投放活动）→ AdGroup（广告组）→ Ad（广告）
 *
 * @author zhuque
 * @version 1.0
 */
```

### Q: 多行文本被格式化乱了？

A: 使用 Java 文本块 `"""` 保持多行排版：
```java
System.out.println("""
    第一行
    第二行
    第三行
    """.formatted());
```

## 参考链接

- [Spotless 官方文档](https://github.com/diffplug/spotless)
- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [VSCode Java 扩展文档](https://code.visualstudio.com/docs/java/java-support)
- [Alibaba Java 开发手册](https://github.com/alibaba/p3c)
- [IntelliJ IDEA Code Style](https://www.jetbrains.com/help/idea/code-style-java.html)
