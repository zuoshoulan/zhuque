# 权限功能开发任务清单

> **开发顺序**: 从底向上，先基础后业务
> **数据库**: MySQL 8.0
> **最后更新**: 2025-01-07
>
> **📖 详细实现文档**: 参见 [design/permission-guide.md](design/permission-guide.md) - 三层权限架构完整技术文档

---

## 📋 开发顺序总览

```
阶段一：数据库层（1-2天）
  ↓
阶段二：后端基础层（2-3天）
  ↓
阶段三：后端业务层（3-4天）
  ↓
阶段四：前端页面（3-4天）
```

---

## 阶段一：数据库层（1-2天）

### 任务1：创建数据库和表

#### 1.1 创建数据库
```sql
CREATE DATABASE zhuque_dev DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 1.2 创建6张核心表

**顺序**：
1. ✅ 用户表 (sys_user)
2. ✅ 角色表 (sys_role)
3. ✅ 权限表 (sys_permission)
4. ✅ 用户角色关联表 (sys_user_role)
5. ✅ 角色权限关联表 (sys_role_permission)
6. ✅ 登录日志表 (sys_login_log)

**初始化数据**：
- 插入超级管理员用户（admin/admin123）
- 插入超级管理员角色
- 插入基础权限数据

**产出**：
- `docker/mysql/init/init.sql` - 初始化脚本（已完成）

---

## 阶段二：后端基础层（2-3天）

### 任务2：项目框架搭建

#### 2.1 创建Maven模块
```
zhuque-commons
  ├── common-core
  ├── common-web
  ├── common-security
  └── common-database

zhuque-model
  ├── entity
  ├── dto
  └── vo

zhuque-dao
zhuque-service-api
zhuque-service
zhuque-web
```

#### 2.2 配置文件
- application.yml - 公共配置
- application-dev.yml - 开发环境配置
- application-prod.yml - 生产环境配置

**产出**：
- 可运行的项目骨架
- 可访问 `http://localhost:8080/actuator/health`

---

### 任务3：JWT工具类开发

#### 3.1 JWT工具类
**位置**: `zhuque-commons/common-security`

**功能**：
- ✅ 生成Token
- ✅ 解析Token获取用户ID
- ✅ 验证Token有效性
- ✅ 刷新Token

**核心方法**：
```java
public class JwtTokenProvider {
    String generateToken(Long userId, String username);
    Long getUserIdFromToken(String token);
    boolean validateToken(String token);
    String generateRefreshToken(Long userId);
}
```

#### 3.2 Spring Security配置

**位置**: `zhuque-commons/common-security`

**功能**：
- ✅ 配置JWT拦截器
- ✅ 配置CORS
- ✅ 配置权限验证
- ✅ 禁用CSRF

**核心类**：
```java
@Configuration
public class SecurityConfig {
    // 配置SecurityFilterChain
    // 配置PasswordEncryptor（BCrypt）
}

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // JWT拦截器
}
```

**产出**：
- 可生成和验证JWT Token
- 访问接口时会验证Token

---

### 任务4：用户权限缓存

#### 4.1 Redis配置
**位置**: `zhuque-commons/common-redis`

**功能**：
- ✅ RedisTemplate配置
- ✅ 序列化配置

#### 4.2 用户权限缓存服务

**位置**: `zhuque-service/auth-service`

**功能**：
- ✅ 缓存用户权限（30分钟）
- ✅ 清除用户权限缓存
- ✅ 批量清除缓存

**缓存结构**：
```
auth:user:info:{userId}         -> 用户基本信息
auth:user:permissions:{userId}  -> 用户权限列表
auth:token:{userId}             -> Token（支持单点登出）
```

**核心方法**：
```java
@Service
public class UserPermissionCache {
    void cacheUserPermissions(Long userId, Set<String> permissions);
    Set<String> getUserPermissions(Long userId);
    void clearUserCache(Long userId);
}
```

**产出**：
- Redis缓存配置完成
- 用户权限可缓存和清除

---

## 阶段三：后端业务层（3-4天）

### 任务5：登录认证接口

#### 5.1 登录接口
**位置**: `zhuque-service/auth-service`

**Controller**:
```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO dto);

    @PostMapping("/logout")
    public Result<Void> logout();

    @PostMapping("/refresh")
    public Result<TokenVO> refreshToken(@RequestParam String refreshToken);

    @GetMapping("/user-info")
    public Result<UserInfoVO> getUserInfo();
}
```

**Service**:
```java
@Service
public class AuthService {
    LoginVO login(String username, String password);
    void logout(String token);
    TokenVO refreshToken(String refreshToken);
    UserInfoVO getUserInfo(Long userId);
}
```

**业务逻辑**：
1. 验证用户名密码
2. 生成JWT Token
3. 查询用户角色和权限
4. 缓存到Redis
5. 记录登录日志

**产出**：
- 可登录获取Token
- 可刷新Token
- 可登出

---

### 任务6：权限验证拦截器

#### 6.1 权限注解
**位置**: `zhuque-commons/common-security`

```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermission {
    String value(); // 如 "advertiser:create"
}
```

#### 6.2 权限拦截器
**位置**: `zhuque-commons/common-security`

```java
@Component
public class PermissionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                            HttpServletResponse response,
                            Object handler);

    private boolean hasPermission(Set<String> permissions, String required);
}
```

**权限验证流程**：
1. 从Token中获取用户ID
2. 从Redis获取用户权限
3. 验证是否拥有所需权限
4. 通过则放行，不通过返回403

**产出**：
- 接口可使用@RequiresPermission注解
- 无权限访问返回403

---

### 任务7：用户管理CRUD

#### 7.1 用户管理接口
**Controller**:
```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping
    public Result<Page<UserVO>> listUsers(UserQuery query);

    @PostMapping
    public Result<Long> createUser(@RequestBody UserCreateDTO dto);

    @PutMapping("/{id}")
    public Result<Void> updateUser(@PathVariable Long id,
                                   @RequestBody UserUpdateDTO dto);

    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id);

    @PostMapping("/{id}/roles")
    public Result<Void> assignRoles(@PathVariable Long id,
                                     @RequestBody List<Long> roleIds);
}
```

**权限要求**：
- 查询: `system:user:list`
- 新增: `system:user:create`
- 编辑: `system:user:update`
- 删除: `system:user:delete`
- 分配角色: `system:user:assign:role`

**产出**：
- 可管理用户
- 可为用户分配角色

---

### 任务8：角色管理CRUD

#### 8.1 角色管理接口
**Controller**:
```java
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @GetMapping
    public Result<List<RoleVO>> listRoles();

    @PostMapping
    public Result<Long> createRole(@RequestBody RoleCreateDTO dto);

    @PutMapping("/{id}")
    public Result<Void> updateRole(@PathVariable Long id,
                                   @RequestBody RoleUpdateDTO dto);

    @DeleteMapping("/{id}")
    public Result<Void> deleteRole(@PathVariable Long id);

    @PostMapping("/{id}/permissions")
    public Result<Void> assignPermissions(@PathVariable Long id,
                                           @RequestBody List<Long> permissionIds);
}
```

**权限要求**：
- 查询: `system:role:list`
- 新增: `system:role:create`
- 编辑: `system:role:update`
- 删除: `system:role:delete`
- 分配权限: `system:role:assign:permission`

**产出**：
- 可管理角色
- 可为角色分配权限

---

### 任务9：权限管理CRUD

#### 9.1 权限管理接口
**Controller**:
```java
@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    @GetMapping("/tree")
    public Result<List<PermissionTreeVO>> getPermissionTree();

    @PostMapping
    public Result<Long> createPermission(@RequestBody PermissionCreateDTO dto);

    @PutMapping("/{id}")
    public Result<Void> updatePermission(@PathVariable Long id,
                                         @RequestBody PermissionUpdateDTO dto);

    @DeleteMapping("/{id}")
    public Result<Void> deletePermission(@PathVariable Long id);
}
```

**权限要求**：
- 查询树: `system:permission:list`
- 新增: `system:permission:create`
- 编辑: `system:permission:update`
- 删除: `system:permission:delete`

**树形结构**：
```json
[
  {
    "id": 1,
    "permissionName": "系统管理",
    "permissionCode": "system",
    "permissionType": 1,
    "children": [
      {
        "id": 10,
        "permissionName": "用户管理",
        "permissionCode": "system:user",
        "permissionType": 1,
        "children": [
          {"id": 101, "permissionName": "查询", "permissionCode": "system:user:list", "permissionType": 3},
          {"id": 102, "permissionName": "新增", "permissionCode": "system:user:create", "permissionType": 2}
        ]
      }
    ]
  }
]
```

**产出**：
- 可管理权限
- 权限树形结构展示

---

## 阶段四：前端页面（3-4天）

### 任务10：登录页面

#### 10.1 登录页面开发
**位置**: `src/views/login/index.vue`

**功能**：
- ✅ 用户名密码输入
- ✅ 登录按钮
- ✅ Token存储到localStorage
- ✅ 登录后跳转首页

**请求**：
```javascript
POST /api/auth/login
{
  "username": "admin",
  "password": "admin123"
}
```

**产出**：
- 可登录获取Token
- 登录后自动跳转

---

### 任务11：路由权限控制

#### 11.1 路由守卫
**位置**: `src/router/guard.ts`

**功能**：
- ✅ 验证Token是否有效
- ✅ Token失效跳转登录页
- ✅ 根据用户权限动态加载路由

**核心逻辑**：
```typescript
router.beforeEach(async (to, from, next) => {
  const token = localStorage.getItem('token')

  if (!token) {
    // 未登录，跳转登录页
    next('/login')
  } else {
    // 验证Token并加载路由
    next()
  }
})
```

**产出**：
- 未登录自动跳转登录页
- Token失效自动跳转登录页

---

### 任务12：权限指令

#### 12.1 v-permission指令
**位置**: `src/directives/permission.ts`

**功能**：
- ✅ 按钮级权限控制
- ✅ 无权限时隐藏按钮

**使用方式**：
```vue
<template>
  <el-button v-permission="'system:user:create'">新增用户</el-button>
  <el-button v-permission="'system:user:delete'">删除</el-button>
</template>
```

**实现**：
```typescript
export const permission = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    const { value } = binding
    const permissions = store.getters.permissions

    if (value && !permissions.includes(value)) {
      el.parentNode?.removeChild(el)
    }
  }
}
```

**产出**：
- 可控制按钮显示/隐藏

---

### 任务13：用户管理页面

#### 13.1 用户列表页面
**位置**: `src/views/system/user/index.vue`

**功能**：
- ✅ 用户列表（表格）
- ✅ 新增用户对话框
- ✅ 编辑用户对话框
- ✅ 删除用户确认
- ✅ 分配角色对话框

**核心组件**：
```vue
<template>
  <el-table :data="users">
    <el-table-column prop="username" label="用户名" />
    <el-table-column prop="realName" label="真实姓名" />
    <el-table-column label="操作">
      <el-button v-permission="'system:user:update'">编辑</el-button>
      <el-button v-permission="'system:user:delete'">删除</el-button>
    </el-table-column>
  </el-table>
</template>
```

**产出**：
- 可管理用户
- 可为用户分配角色

---

### 任务14：角色管理页面

#### 14.1 角色列表页面
**位置**: `src/views/system/role/index.vue`

**功能**：
- ✅ 角色列表
- ✅ 新增角色对话框
- ✅ 编辑角色对话框
- ✅ 删除角色确认
- ✅ **权限分配树形组件** ⭐

**权限树组件**：
```vue
<template>
  <el-tree
    :data="permissionTree"
    :props="{ children: 'children' }"
    show-checkbox
    node-key="id"
    v-model="selectedPermissions"
  />
</template>
```

**产出**：
- 可管理角色
- 可为角色分配权限（树形选择）

---

### 任务15：权限管理页面

#### 15.1 权限列表页面
**位置**: `src/views/system/permission/index.vue`

**功能**：
- ✅ 权限树形表格
- ✅ 新增权限对话框
- ✅ 编辑权限对话框
- ✅ 删除权限确认

**树形表格**：
```vue
<template>
  <el-table
    :data="permissions"
    row-key="id"
    :tree-props="{ children: 'children' }"
  >
    <el-table-column prop="permissionName" label="权限名称" />
    <el-table-column prop="permissionCode" label="权限编码" />
    <el-table-column prop="permissionType" label="类型">
      <template #default="{ row }">
        <el-tag v-if="row.permissionType === 1">菜单</el-tag>
        <el-tag v-else-if="row.permissionType === 2">按钮</el-tag>
        <el-tag v-else>API</el-tag>
      </template>
    </el-table-column>
  </el-table>
</template>
```

**产出**：
- 可管理权限
- 权限树形结构展示

---

## 📊 任务优先级

### P0（必须完成）
- ✅ 任务1: 创建数据库表
- ✅ 任务3: JWT工具类
- ✅ 任务5: 登录接口
- ✅ 任务10: 登录页面

### P1（核心功能）
- ✅ 任务6: 权限验证拦截器
- ✅ 任务7: 用户管理CRUD
- ✅ 任务8: 角色管理CRUD
- ✅ 任务11: 路由权限控制
- ✅ 任务12: 权限指令
- ✅ 任务13: 用户管理页面

### P2（完善功能）
- ✅ 任务4: 用户权限缓存
- ✅ 任务9: 权限管理CRUD
- ✅ 任务14: 角色管理页面
- ✅ 任务15: 权限管理页面

---

## 🎯 验收标准

### 最小可验证版本（MVP）

**后端**：
- ✅ 可以登录获取Token
- ✅ 使用Token可以访问接口
- ✅ 无Token或Token无效返回401
- ✅ 无权限返回403
- ✅ 可以创建用户并分配角色
- ✅ 可以为角色分配权限

**前端**：
- ✅ 登录页面可以登录
- ✅ 登录后显示用户信息
- ✅ 无Token自动跳转登录页
- ✅ 可以管理用户
- ✅ 可以管理角色
- ✅ 按钮权限控制生效

---

## 📅 时间估算

| 阶段 | 任务 | 时间 |
|-----|------|------|
| 阶段一 | 数据库层 | 1-2天 |
| 阶段二 | 后端基础层 | 2-3天 |
| 阶段三 | 后端业务层 | 3-4天 |
| 阶段四 | 前端页面 | 3-4天 |
| **总计** | | **9-13天** |

---

## 🚀 开始顺序

### 第一步：数据库
```bash
# 启动MySQL
docker-compose -f docker-compose-dev.yml up -d mysql

# 执行初始化脚本
mysql -u root -p zhuque_dev < docker/mysql/init/init.sql
```

### 第二步：项目框架
```bash
# 创建Maven项目
# 配置application.yml
# 测试访问 /actuator/health
```

### 第三步：JWT工具类
```bash
# 创建JwtTokenProvider
# 编写单元测试
# 验证生成和验证Token
```

### 第四步：登录接口
```bash
# 实现登录接口
# Postman测试登录
# 获取Token
```

### 第五步：前端登录
```bash
# 创建登录页面
# 测试登录功能
# 验证Token存储
```

---

**文档版本**: v1.0
**最后更新**: 2025-01-07
**维护者**: 开发团队
