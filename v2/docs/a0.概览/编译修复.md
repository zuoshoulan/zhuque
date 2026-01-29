# 编译问题修复记录

## 问题描述

用户反馈后端编译不通过。

## 问题原因

### 1. PermissionCheckHandler 循环依赖
**文件：** `zhuque-common/src/main/java/wake/su/zhuque/common/security/handler/PermissionCheckHandler.java`

**问题：** 该类位于 `zhuque-common` 模块，但依赖了 `zhuque-service-api` 模块的 `PermissionService`，导致循环依赖：
- `zhuque-common` 不能依赖 `zhuque-service-api`
- `zhuque-service` 依赖 `zhuque-common`

**解决方案：** 删除该文件，权限校验逻辑直接在 `PermissionAspect` 中实现。

### 2. JwtUtil 静态方法调用错误
**文件：** `zhuque-common/src/main/java/wake/su/zhuque/common/security/aspect/PermissionAspect.java:86`

**错误：**
```java
return JwtUtil.getUserId(token);  // 错误：getUserId() 不是静态方法
```

**原因：** `JwtUtil` 使用了 `@Component` 注解，是一个 Spring Bean，需要实例化后才能调用。

**解决方案：**
1. 在 `PermissionAspect` 中注入 `JwtUtil`
```java
@RequiredArgsConstructor
public class PermissionAspect {
    private final JwtUtil jwtUtil;
    ...
}
```

2. 使用实例方法调用
```java
return jwtUtil.getUserId(token);  // 正确
```

## 修复步骤

### 1. 删除循环依赖文件
```bash
rm zhuque-backend/zhuque-common/src/main/java/wake/su/zhuque/common/security/handler/PermissionCheckHandler.java
```

### 2. 修改 PermissionAspect
**修改前：**
```java
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {
    // TODO: 注入PermissionService（需要解决循环依赖问题）
    // private final PermissionService permissionService;

    private Long getCurrentUserId() {
        ...
        return JwtUtil.getUserId(token);  // 错误
    }
}
```

**修改后：**
```java
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {
    private final JwtUtil jwtUtil;

    // TODO: 注入PermissionService（需要解决循环依赖问题）
    // private final PermissionService permissionService;

    private Long getCurrentUserId() {
        ...
        return jwtUtil.getUserId(token);  // 正确
    }
}
```

## 验证结果

### 编译成功
```bash
mvn clean compile -DskipTests
# BUILD SUCCESS
```

### 打包成功
```bash
mvn package -DskipTests
# BUILD SUCCESS
# 生成 JAR: web-admin-2.0.0.jar (55M)
```

## 经验总结

### 1. 模块依赖原则
- `zhuque-common` 是基础模块，不应依赖上层模块
- `zhuque-common` 可以被 `zhuque-service`、`zhuque-web` 等依赖
- 避免 `zhuque-common` → `zhuque-service-api` → `zhuque-common` 的循环依赖

### 2. Spring Bean 使用规范
- 使用 `@Component`、`@Service` 等注解的类是非静态的
- 必须通过依赖注入（`@Autowired`、构造函数注入）获取实例
- 不能使用静态方法调用 Spring Bean 的方法

### 3. 权限校验实现方式
- **正确做法：** 在 `PermissionAspect` 中直接实现权限校验逻辑
- **错误做法：** 创建额外的 Handler 类并依赖 Service 层

## 后续优化

### 1. 实现完整的权限校验
目前 `PermissionAspect` 中的权限校验逻辑是TODO状态，需要：
```java
// TODO: 实现权限校验逻辑
// 暂时跳过权限校验，等Redis缓存实现后再完善
log.debug("用户{}请求权限校验：{}，逻辑类型：{}", userId, permissionList, logicalType);
```

**待实现：**
1. 从 Spring Context 获取 `PermissionService` Bean（避免循环依赖）
2. 调用 `permissionService.hasPermissions()` 校验权限
3. 校验失败时抛出 `BusinessException`

### 2. 解决循环依赖问题
**方案1：** 使用 `ApplicationContext` 动态获取 Bean
```java
@Component
public class PermissionAspect {
    private final ApplicationContext applicationContext;

    private PermissionService getPermissionService() {
        return applicationContext.getBean(PermissionService.class);
    }
}
```

**方案2：** 将权限校验逻辑提取到独立模块
- 创建 `zhuque-security` 模块
- 该模块依赖 `zhuque-service-api` 和 `zhuque-common`
- `zhuque-web` 依赖 `zhuque-security`

---

**修复时间：** 2026-01-09 23:16
**修复结果：** ✅ 编译和打包成功
