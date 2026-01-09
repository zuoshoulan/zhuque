# RBAC权限系统测试指南

## 🚀 快速开始

### 1. 启动应用

```bash
cd /home/wake/code/zhuque.worktrees/20260109_v2_dev/v2/zhuque-backend/web-admin
mvn spring-boot:run
```

应用启动在 `http://localhost:8080`

### 2. 初始化数据库

数据库脚本会在应用启动时自动执行，初始化：
- 用户表、角色表、权限表、菜单表等
- 默认管理员账号：`admin` / `Admin123`
- 示例权限和角色数据

---

## 📝 API测试

### 1. 登录获取Token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "Admin123"
  }'
```

响应：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "...",
    "userInfo": {
      "id": 1,
      "username": "admin",
      "nickname": "管理员"
    }
  }
}
```

**保存 `accessToken`，后续请求需要使用**

---

### 2. 测试权限管理API

#### 2.1 获取权限树

```bash
curl -X GET http://localhost:8080/api/permissions/tree \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

#### 2.2 获取角色列表

```bash
curl -X GET http://localhost:8080/api/roles/list \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

#### 2.3 获取角色详情

```bash
curl -X GET http://localhost:8080/api/roles/1 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

#### 2.4 创建角色

```bash
curl -X POST http://localhost:8080/api/roles \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "roleCode": "TEST_ROLE",
    "roleName": "测试角色",
    "description": "用于测试的角色",
    "permissionIds": [1, 2, 3]
  }'
```

#### 2.5 为角色分配权限

```bash
curl -X POST http://localhost:8080/api/roles/2/permissions \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "permissionIds": [1, 2, 3, 7, 8]
  }'
```

---

### 3. 测试菜单API

#### 3.1 获取当前用户菜单树

```bash
curl -X GET http://localhost:8080/api/menus/user/tree \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

#### 3.2 获取所有菜单树

```bash
curl -X GET http://localhost:8080/api/menus/tree \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

---

### 4. 测试@RequiresPermission注解

#### 4.1 创建广告主（需要 advertiser:create 权限）

```bash
curl -X POST http://localhost:8080/api/advertisers \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "测试广告主",
    "contact": "张三"
  }'
```

**预期响应：**
- 如果有权限：返回成功
- 如果无权限：抛出 "权限不足" 异常

#### 4.2 查看广告主详情（需要 advertiser:query 权限）

```bash
curl -X GET http://localhost:8080/api/advertisers/1 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

---

## 🔍 验证权限控制

### 1. 创建测试用户

```sql
INSERT INTO sys_user (username, password, nickname, status)
VALUES ('test_user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '测试用户', 1);
-- 密码是: Admin123
```

### 2. 为测试用户分配普通角色（无广告主管理权限）

```sql
INSERT INTO sys_user_role (user_id, role_id) VALUES (2, 2);
-- 假设 test_user 的 ID 是 2，为其分配"管理员"角色（ID=2）
```

### 3. 用测试用户登录，尝试访问需要权限的接口

```bash
# 登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test_user",
    "password": "Admin123"
  }'

# 尝试创建广告主（应该失败，因为test_user没有 advertiser:create 权限）
curl -X POST http://localhost:8080/api/advertisers \
  -H "Authorization: Bearer TEST_USER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name": "测试"}'
```

**预期结果：** 返回 "权限不足" 错误

### 4. 为测试用户添加权限

```sql
-- 先找到 advertiser:create 权限的ID（假设是2）
-- 为角色2添加该权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (2, 2);

-- 或者直接为用户分配超级管理员角色
INSERT INTO sys_user_role (user_id, role_id) VALUES (2, 1);
```

再次测试，应该可以访问

---

## 🧪 数据库验证

### 1. 查看用户权限

```sql
-- 查看用户1的所有角色
SELECT r.* FROM sys_role r
INNER JOIN sys_user_role ur ON r.id = ur.role_id
WHERE ur.user_id = 1;

-- 查看用户1的所有权限
SELECT DISTINCT p.* FROM sys_permission p
INNER JOIN sys_role_permission rp ON p.id = rp.permission_id
INNER JOIN sys_user_role ur ON rp.role_id = ur.role_id
WHERE ur.user_id = 1;

-- 查看用户1的权限编码
SELECT DISTINCT p.permission_code FROM sys_permission p
INNER JOIN sys_role_permission rp ON p.id = rp.permission_id
INNER JOIN sys_user_role ur ON rp.role_id = ur.role_id
WHERE ur.user_id = 1 AND p.status = 1;
```

### 2. 查看权限树

```sql
-- 查看所有权限（树形结构）
SELECT * FROM sys_permission
WHERE status = 1 AND deleted = 0
ORDER BY parent_id, sort_order;
```

---

## 📊 性能测试

### 1. 压力测试权限校验

使用Apache Bench (ab) 进行压力测试：

```bash
# 安装ab
sudo apt install apache2-utils

# 测试权限校验性能（1000个请求，100并发）
ab -n 1000 -c 100 \
   -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
   http://localhost:8080/api/advertisers
```

### 2. 监控SQL查询

启用MyBatis-Plus SQL日志：

```yaml
# application.yml
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

观察权限校验时的SQL查询次数

**优化目标：**
- ✅ 使用Redis缓存用户权限
- ✅ 减少数据库查询次数
- ✅ 权限变更时清除缓存

---

## ❗ 常见问题

### 1. 权限校验不生效？

**可能原因：**
- PermissionAspect切面未生效
- JWT Token无效或过期
- 用户未分配角色或权限

**排查方法：**
1. 检查应用启动日志，确认PermissionAspect已加载
2. 使用 `/api/auth/user-info` 接口验证Token
3. 查询数据库确认用户权限配置

### 2. 如何临时禁用权限校验？

**方法1：** 删除Controller上的 `@RequiresPermission` 注解

**方法2：** 在PermissionAspect中临时注释掉校验逻辑

```java
@Before("@annotation(requiresPermission)")
public void checkPermission(...) {
    // 临时注释掉校验逻辑
    // if (!hasPermission) { throw new BusinessException("权限不足"); }
    log.debug("权限校验已禁用");
}
```

### 3. 如何添加新的权限？

**步骤：**
1. 在数据库中插入权限记录
2. 创建对应的角色或为现有角色分配权限
3. 为用户分配角色
4. 在Controller方法上添加 `@RequiresPermission` 注解

---

## 📚 参考文档

- [RBAC设计文档](design/RBAC_DESIGN.md)
- [实现总结](overview/RBAC_IMPLEMENTATION.md)
- [项目状态](overview/PROJECT_STATUS.md)

---

**文档版本：** v1.0
**编写日期：** 2026-01-09
**作者：** wake.su
