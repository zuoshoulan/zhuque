# 朱雀广告平台 - 项目开发规范

> 本文件定义了项目的编码规范和约定,Claude Code 在执行任何任务前必须先阅读并严格遵守这些规范。

## 最后更新时间
2026-01-08

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

#### 6.2 实体类注解
```java
@Data
@TableName("sys_user")
public class SysUserDO {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

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

## 重要提醒

⚠️ **每次执行任务前,必须先阅读本配置文件!**

⚠️ **遇到冲突时,以本配置文件为准!**

⚠️ **本配置文件会持续更新,每次更新后需要重新读取!**
