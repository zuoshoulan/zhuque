# 朱雀广告平台 - 项目开发规范

> 本文件定义了项目的编码规范和约定,Claude Code 在执行任何任务前必须先阅读并严格遵守这些规范。

## 最后更新时间
2026-01-10

## 0. 对话与交流规范

### 0.1 默认语言
- ✅ **必须使用中文进行所有交流和回复**
- ❌ 禁止使用英文回复（除非涉及代码、技术术语）
- 示例：
  - ✅ 正确: "用户登录功能已实现，需要添加JWT验证"
  - ❌ 错误: "User login feature implemented, need to add JWT"

### 0.2 代码注释语言
- Java代码注释: 必须使用中文
- TypeScript/Vue代码注释: 必须使用中文
- 变量命名: 使用英文（遵循驼峰命名法）

### 0.3 技术术语处理
- 技术名词使用英文: JWT, API, SQL, HTTP, REST等
- 但解释和说明使用中文
- 示例: "使用JWT进行身份验证，Token有效期为24小时"

## 核心规范

### 1. 命名规范

#### 1.1 实体类命名
- ✅ **必须使用 DO 后缀**
- 示例: `SysUserDO`, `SysRoleDO`, `SysPermissionDO`
- ❌ 禁止: `SysUser`, `SysRole`, `SysPermission`

#### 1.2 DTO命名
- 使用明确的DTO后缀
- 示例: `LoginRequest`, `LoginResponse`, `UserVO`

#### 1.3 日志文件命名
- 格式: `{服务名}-{级别}.log`
- 示例: `service-info.log`, `service-error.log`
- ❌ 禁止: `info-service.log`, `error-service.log`

### 2. 依赖注入规范

#### 2.1 注解选择
- ✅ **必须使用 `@Resource`**
- ❌ **禁止使用 `@Autowired`**
- 示例:
```java
@Resource
private SysUserService sysUserService;
```

### 3. Service层设计规范

#### 3.1 继承与组合
- ✅ **使用组合方式**
- ❌ **禁止继承 `ServiceImpl`**
- 示例:
```java
// ✅ 正确
@Service
public class SysUserServiceImpl implements SysUserService {
    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    public SysUserDO getById(Long userId) {
        return sysUserMapper.selectById(userId);
    }
}

// ❌ 错误
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUserDO> {
    // 空实现
}
```

#### 3.2 方法实现
- 所有业务方法必须明确实现
- 添加清晰的日志记录
- 添加完整的JavaDoc注释

### 4. 异常处理规范

#### 4.1 全局异常处理器
- 使用 `@RestControllerAdvice(basePackages = "wake.su.zhuque.controller")`
- 只处理controller包下的异常
- 不影响Actuator等非业务端点

### 5. 日志规范

#### 5.1 日志级别使用
- `ERROR`: 错误日志
- `WARN`: 警告日志
- `INFO`: 关键业务操作
- `DEBUG`: 调试信息

#### 5.2 日志格式
- 使用 `@Slf4j` 注解
- 示例:
```java
log.info("用户登录: account={}", account);
log.debug("根据ID查询用户: userId={}", userId);
log.error("系统异常", e);
```

### 6. 数据库规范

#### 6.1 字符编码
- 数据库连接URL必须使用: `characterEncoding=UTF-8`
- 表字符集: `utf8mb4`
- 排序规则: `utf8mb4_0900_ai_ci`

#### 6.1.1 数据库时间戳字段定义规范
⚠️ **禁止使用数据库自动更新时间戳**

**数据库表定义规范:**
```sql
-- ✅ 正确: 不使用 ON UPDATE CURRENT_TIMESTAMP
CREATE TABLE `sys_user` (
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',  -- 没有 ON UPDATE
  `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
  `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ❌ 错误: 使用 ON UPDATE CURRENT_TIMESTAMP
CREATE TABLE `sys_user` (
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',  -- 禁止!
  `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
  `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

**为什么禁止数据库自动更新:**
1. ❌ 无法记录更新人(`update_by`)的准确信息
2. ❌ 无法在业务逻辑中控制更新时机
3. ❌ 不符合审计字段需要同时记录"谁在何时修改"的要求
4. ✅ Java代码显式控制更明确、更可追溯

#### 6.2 时间戳和审计字段管理
⚠️ **核心原则: 不要依赖数据库默认值,必须在Java代码中显式设置**

**必须显式设置的字段:**
- `create_time`: 创建时间
- `update_time`: 更新时间
- `create_by`: 创建人
- `update_by`: 更新人

**实现规范:**

1. **INSERT操作时必须设置:**
```java
// ✅ 正确: 在插入前显式设置所有审计字段
SysUserDO user = new SysUserDO();
user.setUsername("admin");
user.setCreateTime(LocalDateTime.now());  // 必须设置
user.setCreateBy(getCurrentUsername());   // 必须设置
sysUserMapper.insert(user);

// ❌ 错误: 依赖数据库DEFAULT值或自动填充
SysUserDO user = new SysUserDO();
user.setUsername("admin");
sysUserMapper.insert(user);  // create_time 和 create_by 可能为 null
```

2. **UPDATE操作时必须设置:**
```java
// ✅ 正确: 更新时显式设置 update_time 和 update_by
SysUserDO user = new SysUserDO();
user.setId(1L);
user.setUsername("new_admin");
user.setUpdateTime(LocalDateTime.now());  // 必须设置
user.setUpdateBy(getCurrentUsername());   // 必须设置
sysUserMapper.updateById(user);

// ❌ 错误: 依赖数据库触发器或自动填充
SysUserDO user = new SysUserDO();
user.setId(1L);
user.setUsername("new_admin");
sysUserMapper.updateById(user);  // update_time 和 update_by 不会被更新
```

3. **批量插入时也要设置:**
```java
// ✅ 正确: 批量插入时为每个记录设置审计字段
List<SysUserRoleDO> userRoleList = roleIds.stream()
    .map(roleId -> {
        SysUserRoleDO userRole = new SysUserRoleDO();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        userRole.setCreateTime(LocalDateTime.now());  // 必须设置
        userRole.setCreateBy(getCurrentUsername());   // 必须设置
        return userRole;
    })
    .collect(Collectors.toList());

userRoleList.forEach(sysUserRoleMapper::insert);
```

4. **实体类字段定义:**
```java
@Data
@TableName("sys_user")
public class SysUserDO {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // ⚠️ 必须显式使用 @TableField 注解指定数据库字段名
    // 不要依赖 MyBatis-Plus 的自动填充功能
    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("create_by")
    private String createBy;

    @TableField("update_by")
    private String updateBy;

    @TableLogic
    private Integer deleted;
}
```

**重要说明:**
- ✅ **必须**使用 `@TableField("create_time")` 显式指定数据库字段名
- ✅ **禁止**依赖 MyBatis-Plus 的驼峰自动映射
- ✅ **禁止**使用 `@TableField(fill = FieldFill.INSERT)` 自动填充
- ✅ 所有审计字段(create_time, update_time, create_by, update_by)都必须添加 `@TableField` 注解

**为什么这个规范很重要:**
- ✅ 确保数据完整性: 避免因数据库配置不同导致的数据不一致
- ✅ 明确性: 代码中明确知道谁在何时创建/修改了数据
- ✅ 可追溯性: 审计字段准确反映操作人和操作时间
- ✅ 避免空值错误: 防止 `Column 'create_time' cannot be null` 等错误

**获取当前用户工具方法:**
```java
private String getCurrentUsername() {
    // 从 Spring Security 上下文获取当前登录用户
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated()) {
        return authentication.getName();
    }
    return "system";  // 默认值
}
```

#### 6.3 实体类注解
除了时间戳字段外的其他注解规范:
```java
@Data
@TableName("sys_user")
public class SysUserDO {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableLogic
    private Integer deleted;
}
```

### 7. 配置文件规范

#### 7.1 配置外部化
- 敏感信息配置到 `application-dev.yml`
- 通用配置配置到 `application.yml`

#### 7.2 命名规范
- 环境配置文件: `application-{profile}.yml`
- 示例: `application-dev.yml`, `application-prod.yml`

### 8. API接口规范

#### 8.1 返回格式
统一使用 `Result<T>` 格式:
```java
Result.success(data)
Result.error("错误信息")
Result.error(400, "参数错误")
```

#### 8.2 RESTful规范
- GET: 查询
- POST: 创建
- PUT: 更新
- DELETE: 删除

### 9. 代码注释规范

#### 9.1 类注释
```java
/**
 * 系统用户服务实现
 */
@Slf4j
@Service
public class SysUserServiceImpl implements SysUserService {
}
```

#### 9.2 方法注释
```java
/**
 * 根据ID查询用户
 *
 * @param userId 用户ID
 * @return 用户信息
 */
public SysUserDO getById(Long userId) {
    return sysUserMapper.selectById(userId);
}
```

### 10. Git提交规范

#### 10.1 Commit Message格式
```
<type>: <subject>

<body>
```

#### 10.2 Type类型
- `feat`: 新功能
- `fix`: 修复bug
- `refactor`: 重构
- `docs`: 文档
- `style`: 格式
- `test`: 测试
- `chore`: 构建/工具

## 技术栈信息

### 核心框架
- Spring Boot: 3.2.1
- JDK: 21
- 包名: `wake.su.zhuque`

### 数据库
- MySQL: 8.0.33
- Redis: 7.4.1
- 连接池: HikariCP

### 持久层
- MyBatis-Plus: 3.5.15 (使用 `mybatis-plus-spring-boot3-starter`)

### 安全认证
- Spring Security
- JWT: 0.12.3

### 工具类
- Lombok
- Hutool: 5.8.24
- Commons Lang3: 3.14.0

### 11. 应用程序管理规范

#### 11.1 测试后必须关闭应用程序
⚠️ **非常重要: 测试完成后必须关闭所有前后端应用程序!**

**前端应用关闭流程:**
```bash
# 1. 查找前端进程
ps aux | grep -E "(npm|vite|node.*zhuque-frontend)" | grep -v grep

# 2. 如果有进程在运行,使用 kill 命令关闭
kill -9 <进程ID>

# 3. 或者查找并关闭占用3000端口的进程
lsof -ti:3000 | xargs kill -9
```

**后端应用关闭流程:**
```bash
# 1. 查找后端Java进程
ps aux | grep "zhuque-backend" | grep -v grep

# 2. 或者查找占用8080端口的进程
lsof -ti:8080 | xargs kill -9

# 3. 如果是使用 nohup 启动的,查找并关闭 nohup 进程
ps aux | grep "nohup.*java" | grep -v grep
```

**验证关闭成功:**
```bash
# 确认没有进程在运行
lsof -ti:3000
lsof -ti:8080

# 上述命令应该返回空(没有输出)
```

#### 11.2 应用程序启动命令记录

**前端启动:**
```bash
cd /home/wake/code/zhuque.worktrees/20260109_v2_dev/v2/zhuque-frontend
npm run dev
# 前端运行在: http://localhost:3000
```

**后端启动:**
```bash
cd /home/wake/code/zhuque.worktrees/20260109_v2_dev/v2/zhuque-backend
mvn clean package -DskipTests
nohup java -jar web-admin/target/web-admin-2.0.0.jar > app.log 2>&1 &
# 后端运行在: http://localhost:8080
```

#### 11.3 开发工作流程
1. ✅ 启动应用程序(如果未启动)
2. ✅ 进行开发和测试
3. ✅ **测试完成后立即关闭应用程序**
4. ✅ 验证所有进程已终止
5. ✅ 记录任何重要的发现或问题

## 重要提醒

⚠️ **每次执行任务前,必须先阅读本配置文件!**

⚠️ **遇到冲突时,以本配置文件为准!**

⚠️ **本配置文件会持续更新,每次更新后需要重新读取!**

⚠️ **⚠️ 测试完成后必须关闭所有前后端应用程序!这是强制要求!**
