# 认证授权模块设计文档

> **模块版本**: v1.0
> **编写日期**: 2025-01-07
> **状态**: 📝 设计中

---

## 一、模块概述

### 1.1 功能描述
认证授权模块是整个系统的基础安全模块，负责：
- 用户身份认证（登录、登出、Token刷新）
- 权限控制（RBAC三层权限模型）
- 用户管理、角色管理、权限管理
- 菜单权限配置
- 登录日志审计

### 1.2 业务目标
- 确保系统安全，防止未授权访问
- 提供灵活的权限配置能力
- 支持多用户类型（管理员、广告主、运营等）
- 提供良好的用户体验（单点登录、记住密码等）

### 1.3 依赖关系
**依赖模块**:
- zhuque-commons（公共工具、安全注解）
- zhuque-model（用户、角色、权限实体）
- zhuque-dao（数据访问层）

**被依赖模块**:
- 所有业务模块（都需要认证授权）

---

## 二、数据模型

### 2.1 数据库表设计

#### 用户表 (sys_user)
```sql
CREATE TABLE sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
  password VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
  real_name VARCHAR(50) COMMENT '真实姓名',
  email VARCHAR(100) COMMENT '邮箱',
  phone VARCHAR(20) COMMENT '手机号',
  avatar VARCHAR(255) COMMENT '头像URL',
  status TINYINT DEFAULT 1 COMMENT '状态：1-正常 0-禁用',
  user_type TINYINT DEFAULT 1 COMMENT '用户类型：1-管理员 2-广告主 3-运营',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_username (username),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';
```

#### 角色表 (sys_role)
```sql
CREATE TABLE sys_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
  role_name VARCHAR(50) UNIQUE NOT NULL COMMENT '角色名称',
  role_code VARCHAR(50) UNIQUE NOT NULL COMMENT '角色编码',
  description VARCHAR(200) COMMENT '角色描述',
  status TINYINT DEFAULT 1 COMMENT '状态：1-正常 0-禁用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';
```

#### 权限表 (sys_permission)
```sql
CREATE TABLE sys_permission (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
  permission_name VARCHAR(50) NOT NULL COMMENT '权限名称',
  permission_code VARCHAR(100) UNIQUE NOT NULL COMMENT '权限编码（如：advertiser:create）',
  permission_type TINYINT NOT NULL COMMENT '权限类型：1-菜单 2-按钮 3-API',
  parent_id BIGINT DEFAULT 0 COMMENT '父权限ID',
  path VARCHAR(200) COMMENT '路由路径（菜单类型）',
  component VARCHAR(200) COMMENT '组件路径（菜单类型）',
  icon VARCHAR(100) COMMENT '图标',
  sort_order INT DEFAULT 0 COMMENT '排序号',
  status TINYINT DEFAULT 1 COMMENT '状态：1-正常 0-禁用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_permission_code (permission_code),
  INDEX idx_permission_type (permission_type),
  INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统权限表';
```

#### 用户角色关联表 (sys_user_role)
```sql
CREATE TABLE sys_user_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL COMMENT '用户ID',
  role_id BIGINT NOT NULL COMMENT '角色ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_user_role (user_id, role_id),
  INDEX idx_user_id (user_id),
  INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';
```

#### 角色权限关联表 (sys_role_permission)
```sql
CREATE TABLE sys_role_permission (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_id BIGINT NOT NULL COMMENT '角色ID',
  permission_id BIGINT NOT NULL COMMENT '权限ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_role_permission (role_id, permission_id),
  INDEX idx_role_id (role_id),
  INDEX idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';
```

#### 登录日志表 (sys_login_log)
```sql
CREATE TABLE sys_login_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
  user_id BIGINT COMMENT '用户ID',
  username VARCHAR(50) COMMENT '用户名',
  login_ip VARCHAR(50) COMMENT '登录IP',
  login_location VARCHAR(100) COMMENT '登录地点',
  browser VARCHAR(50) COMMENT '浏览器',
  os VARCHAR(50) COMMENT '操作系统',
  status TINYINT COMMENT '登录状态：1-成功 0-失败',
  msg VARCHAR(255) COMMENT '提示信息',
  login_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  INDEX idx_user_id (user_id),
  INDEX idx_login_time (login_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';
```

### 2.2 实体关系图

```
┌─────────────┐         ┌──────────────┐         ┌─────────────────┐
│  sys_user   │────────>│ sys_user_role│<────────│   sys_role      │
│   (用户)    │         │  (用户角色)  │         │    (角色)       │
└─────────────┘         └──────────────┘         └────────┬────────┘
                                                          │
                                                          │
                                                  ┌───────▼──────────┐
                                                  │sys_role_permission│
                                                  │   (角色权限)      │
                                                  └───────┬──────────┘
                                                          │
                                                          │
                                                  ┌───────▼──────────┐
                                                  │ sys_permission   │
                                                  │    (权限)        │
                                                  └──────────────────┘
```

### 2.3 索引策略
- 用户名唯一索引：快速登录查询
- 权限编码唯一索引：权限检查
- 联合唯一索引：防止重复关联
- 时间索引：日志查询

---

## 三、接口设计

### 3.1 认证接口

#### 1. 用户登录
```
POST /api/auth/login
Content-Type: application/json

Request:
{
  "username": "admin",
  "password": "admin123",
  "captcha": "1234",
  "uuid": "captcha-uuid"
}

Response (200):
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 7200,
    "userInfo": {
      "id": 1,
      "username": "admin",
      "realName": "管理员",
      "avatar": "http://example.com/avatar.jpg",
      "email": "admin@example.com",
      "roles": ["admin"],
      "permissions": ["*:*:*"]
    }
  }
}
```

#### 2. 刷新Token
```
POST /api/auth/refresh

Request:
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}

Response (200):
{
  "code": 200,
  "data": {
    "token": "new-token...",
    "refreshToken": "new-refresh-token...",
    "expiresIn": 7200
  }
}
```

#### 3. 登出
```
POST /api/auth/logout
Headers: Authorization: Bearer {token}

Response (200):
{
  "code": 200,
  "message": "登出成功"
}
```

#### 4. 获取当前用户信息
```
GET /api/auth/user-info
Headers: Authorization: Bearer {token}

Response (200):
{
  "code": 200,
  "data": {
    "id": 1,
    "username": "admin",
    "realName": "管理员",
    "avatar": "http://example.com/avatar.jpg",
    "roles": [{
      "id": 1,
      "roleName": "超级管理员",
      "roleCode": "admin"
    }],
    "permissions": [
      {
        "id": 1,
        "permissionName": "广告主管理",
        "permissionCode": "advertiser:*",
        "permissionType": 1
      }
    ]
  }
}
```

### 3.2 用户管理接口

#### 1. 用户列表（分页）
```
GET /api/users?page=1&size=10&username=admin
Headers: Authorization: Bearer {token}
```

#### 2. 创建用户
```
POST /api/users
Headers: Authorization: Bearer {token}

Request:
{
  "username": "testuser",
  "password": "123456",
  "realName": "测试用户",
  "email": "test@example.com",
  "roleIds": [2, 3]
}
```

#### 3. 更新用户
```
PUT /api/users/{id}
```

#### 4. 删除用户
```
DELETE /api/users/{id}
```

#### 5. 分配角色
```
POST /api/users/{id}/roles
Headers: Authorization: Bearer {token}

Request:
{
  "roleIds": [2, 3, 4]
}
```

### 3.3 角色管理接口

#### 1. 角色列表
```
GET /api/roles
```

#### 2. 创建角色
```
POST /api/roles
Request:
{
  "roleName": "广告主管理员",
  "roleCode": "advertiser_admin",
  "description": "负责广告主管理",
  "permissionIds": [10, 11, 12, 20, 21]
}
```

#### 3. 分配权限
```
POST /api/roles/{id}/permissions
Request:
{
  "permissionIds": [10, 11, 12, 20, 21, 22]
}
```

### 3.4 权限管理接口

#### 1. 权限树形列表
```
GET /api/permissions/tree
Response:
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "permissionName": "广告管理",
      "permissionCode": "ad",
      "permissionType": 1,
      "children": [
        {
          "id": 10,
          "permissionName": "广告主管理",
          "permissionCode": "advertiser:*",
          "permissionType": 1,
          "children": [
            {"id": 101, "permissionName": "查询", "permissionCode": "advertiser:list", "permissionType": 3},
            {"id": 102, "permissionName": "新增", "permissionCode": "advertiser:create", "permissionType": 2},
            {"id": 103, "permissionName": "编辑", "permissionCode": "advertiser:update", "permissionType": 2},
            {"id": 104, "permissionName": "删除", "permissionCode": "advertiser:delete", "permissionType": 2}
          ]
        }
      ]
    }
  ]
}
```

### 3.5 权限要求说明

所有接口（除登录、登出外）都需要携带JWT Token：
```
Headers: Authorization: Bearer {token}
```

部分接口还需要特定的权限标识：
```
@RequiresPermission("advertiser:create")
```

---

## 四、业务逻辑

### 4.1 登录流程

```
┌─────────┐      ┌─────────┐      ┌─────────┐      ┌─────────┐
│ 前端表单 │ ───> │ 后端接口 │ ───> │ 查库验证 │ ───> │ 生成Token│
└─────────┘      └─────────┘      └─────────┘      └─────────┘
                                           │              │
                                           ▼              ▼
                                    ┌─────────┐      ┌─────────┐
                                    │ 密码错误 │      │ 缓存权限 │
                                    │ 记录日志 │      │ 返回结果 │
                                    └─────────┘      └─────────┘
```

**步骤详解**：
1. 前端提交用户名、密码、验证码
2. 后端验证验证码（可选）
3. 查询数据库，验证用户名和密码（BCrypt）
4. 密码错误：记录登录日志，返回错误
5. 密码正确：
   - 生成JWT Token（2小时有效期）
   - 生成RefreshToken（7天有效期）
   - 查询用户角色和权限
   - 缓存到Redis（key: `auth:user:info:{userId}`）
   - 记录登录日志
   - 返回Token和用户信息

### 4.2 权限验证流程

```
                     ┌──────────────┐
                     │  请求携带Token │
                     └──────┬───────┘
                            │
                            ▼
                    ┌───────────────┐
                    │ JWT拦截器验证  │
                    └───────┬───────┘
                            │
                 ┌──────────┴──────────┐
                 │                     │
                 ▼                     ▼
            ┌────────┐          ┌────────────┐
            │ Token无效│         │  Token有效  │
            └────────┘          └─────┬──────┘
                                      │
                                      ▼
                              ┌───────────────┐
                              │ 从Redis获取权限 │
                              └───────┬───────┘
                                      │
                           ┌──────────┴──────────┐
                           │                     │
                           ▼                     ▼
                    ┌────────────┐        ┌────────────┐
                    │ Redis未命中 │        │ Redis命中   │
                    └─────┬──────┘        └─────┬──────┘
                          │                     │
                          ▼                     │
                  ┌──────────────┐              │
                  │ 查询数据库    │              │
                  │ 写入Redis     │              │
                  └──────┬───────┘              │
                         │                      │
                         └──────────┬───────────┘
                                    │
                                    ▼
                            ┌──────────────┐
                            │ 检查接口权限  │
                            └──────┬───────┘
                                   │
                        ┌──────────┴──────────┐
                        │                     │
                        ▼                     ▼
                   ┌────────┐           ┌────────┐
                   │ 无权限  │           │ 有权限  │
                   │ 返回403 │           │ 放行请求 │
                   └────────┘           └────────┘
```

### 4.3 业务规则

1. **密码规则**
   - 最小长度：6位
   - 加密方式：BCrypt
   - 不允许明文存储

2. **Token规则**
   - Access Token有效期：2小时
   - Refresh Token有效期：7天
   - 存储方式：Redis（key: `auth:token:{userId}`）

3. **权限规则**
   - 支持通配符：`advertiser:*` 代表所有广告主权限
   - 超级管理员：拥有 `*:*:*` 权限
   - 权限缓存：30分钟

4. **用户状态**
   - 正常：可登录
   - 禁用：无法登录，即使密码正确

### 4.4 异常处理

| 异常场景 | 错误码 | 提示信息 |
|---------|-------|---------|
| 用户名不存在 | 401 | 用户名或密码错误 |
| 密码错误 | 401 | 用户名或密码错误 |
| 用户被禁用 | 403 | 账号已被禁用，请联系管理员 |
| Token过期 | 401 | 登录已过期，请重新登录 |
| Token无效 | 401 | 非法访问 |
| 无权限 | 403 | 没有访问权限 |
| 验证码错误 | 400 | 验证码错误 |

---

## 五、技术实现

### 5.1 关键代码示例

#### JWT工具类
```java
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpiration);

        return Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId()))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty");
        }
        return false;
    }
}
```

#### 权限注解
```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasPermission(#permission)")
public @interface RequiresPermission {
    String value();
}
```

#### 权限拦截器
```java
@Component
public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request,
                            HttpServletResponse response,
                            Object handler) throws Exception {
        // 获取当前用户
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            sendError(response, 401, "未登录");
            return false;
        }

        // 检查方法上的权限注解
        RequiresPermission permission = getPermissionAnnotation(handler);
        if (permission == null) {
            return true; // 不需要权限
        }

        // 从Redis获取用户权限
        Set<String> userPermissions = getUserPermissions(userId);

        // 检查是否有权限
        if (!hasPermission(userPermissions, permission.value())) {
            sendError(response, 403, "没有访问权限");
            return false;
        }

        return true;
    }

    private boolean hasPermission(Set<String> permissions, String required) {
        return permissions.stream().anyMatch(p -> {
            if (p.equals("*:*:*")) return true; // 超级管理员
            if (p.equals(required)) return true; // 精确匹配
            // 通配符匹配
            String regex = p.replace("*", ".*");
            return required.matches(regex);
        });
    }
}
```

### 5.2 性能优化

1. **权限缓存**
   - 使用Redis缓存用户权限（30分钟）
   - 权限变更时主动刷新缓存
   - 缓存Key：`auth:user:permissions:{userId}`

2. **Token缓存**
   - 存储到Redis，支持单点登出
   - 缓存Key：`auth:token:{userId}`

3. **懒加载**
   - 登录时只加载基础用户信息
   - 首次访问时加载权限并缓存

### 5.3 缓存策略

| 数据类型 | 缓存Key | 过期时间 | 刷新策略 |
|---------|---------|---------|---------|
| 用户信息 | auth:user:info:{userId} | 30分钟 | 主动刷新 |
| 用户权限 | auth:user:permissions:{userId} | 30分钟 | 主动刷新 |
| Token | auth:token:{userId} | 2小时 | 自动过期 |
| 角色权限 | auth:role:permissions:{roleId} | 1小时 | 主动刷新 |

---

## 六、测试要点

### 6.1 单元测试

- [ ] JWT工具类测试（生成、解析、验证）
- [ ] 密码加密测试（BCrypt）
- [ ] 权限匹配逻辑测试（通配符）
- [ ] Token刷新逻辑测试

### 6.2 集成测试

- [ ] 登录成功场景
- [ ] 登录失败场景（密码错误、用户不存在）
- [ ] Token过期场景
- [ ] 权限验证场景（有权限、无权限）
- [ ] 角色权限分配场景
- [ ] 权限缓存刷新场景

### 6.3 边界条件

- [ ] 用户名包含特殊字符
- [ ] 密码长度超限
- [ ] 并发登录
- [ ] Token被篡改
- [ ] Redis连接失败

### 6.4 安全测试

- [ ] SQL注入测试
- [ ] XSS攻击测试
- [ ] 暴力破解防护
- [ ] Token劫持防护

---

## 七、开发进度

### 待办事项

- [ ] **数据层**
  - [ ] 创建数据库表
  - [ ] 生成MyBatis实体和Mapper
  - [ ] 编写基础CRUD方法

- [ ] **服务层**
  - [ ] 实现登录逻辑
  - [ ] 实现Token刷新
  - [ ] 实现权限验证
  - [ ] 实现用户管理
  - [ ] 实现角色管理
  - [ ] 实现权限管理

- [ ] **接口层**
  - [ ] 认证接口（登录、登出、刷新）
  - [ ] 用户管理接口
  - [ ] 角色管理接口
  - [ ] 权限管理接口

- [ ] **安全配置**
  - [ ] Spring Security配置
  - [ ] JWT拦截器
  - [ ] 权限拦截器
  - [ ] CORS配置

- [ ] **前端页面**
  - [ ] 登录页面
  - [ ] 用户管理页面
  - [ ] 角色管理页面
  - [ ] 权限管理页面

### 完成状态

| 功能模块 | 状态 | 完成日期 |
|---------|------|---------|
| 数据库设计 | 📝 设计中 | - |
| 登录接口 | ⏳ 待开发 | - |
| 权限验证 | ⏳ 待开发 | - |
| 前端页面 | ⏳ 待开发 | - |

### 遇到的问题

（记录开发过程中遇到的问题和解决方案）

---

## 八、参考资料

- [Spring Security官方文档](https://docs.spring.io/spring-security/reference/)
- [JWT规范](https://jwt.io/)
- [RBAC权限设计](../design/RBAC_DESIGN.md)
- [API设计规范](../design/API_DESIGN.md)

---

**文档版本**: v1.0
**最后更新**: 2025-01-07
**维护者**: 开发团队
