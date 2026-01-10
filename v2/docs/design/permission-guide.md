# 朱雀广告平台 - 完整权限架构设计文档

## 目录
- [一、架构概览](#一架构概览)
- [二、权限设计理念](#二权限设计理念)
- [三、数据库设计](#三数据库设计)
- [四、后端实现](#四后端实现)
- [五、前端实现](#五前端实现)
- [六、权限配置策略](#六权限配置策略)
- [七、实际应用场景](#七实际应用场景)
- [八、权限继承机制](#八权限继承机制)
- [九、性能优化](#九性能优化)
- [十、常见问题](#十常见问题)

---

## 一、架构概览

### 1.1 三层权限架构图

```
┌─────────────────────────────────────────────────────────────────┐
│                    朱雀广告平台三层权限架构                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  用户登录                                                        │
│    ↓                                                             │
│  查询用户权限（菜单 + 按钮 + 接口）                               │
│    ├─ 第一层：菜单权限(permission_type=1)                        │
│  └─ 第二层：按钮权限(permission_type=2)                         │
│  └─ 第三层：接口权限(permission_type=3)                          │
│    ↓                                                             │
│  前端渲染                                                        │
│    ├─ 菜单树：根据第一层权限生成                                  │
│    ├─ 按钮显示：根据第二层权限控制                                │
│    └─ 操作触发：调用后端API                                      │
│    ↓                                                             │
│  后端校验                                                        │
│    └─ API权限：根据第三层权限校验                                │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 1.2 权限层次详解

#### 第一层：菜单权限
```
┌─────────────────────────────────────────┐
│ 菜单权限 (permission_type=1)            │
├─────────────────────────────────────────┤
│ • 权限编码: advertiser                  │
│ • 控制内容: 前端页面/路由可见性          │
│ • 校验位置: 前端路由守卫                │
│ • 数据来源: sys_menu.permission_code   │
│ • 示例:                                │
│   - advertiser (广告主管理)             │
│   - system:user (用户管理)              │
│   - system:role (角色管理)              │
└─────────────────────────────────────────┘
```

#### 第二层：按钮权限
```
┌─────────────────────────────────────────┐
│ 按钮权限 (permission_type=2)            │
├─────────────────────────────────────────┤
│ • 权限编码: advertiser:create           │
│ • 控制内容: 前端按钮/操作可见性          │
│ • 校验位置: 前端v-if指令                 │
│ • 数据来源: sys_permission表            │
│ • 示例:                                │
│   - advertiser:create (新增)            │
│   - advertiser:update (编辑)            │
│   - advertiser:delete (删除)            │
│   - advertiser:query (查看)             │
└─────────────────────────────────────────┘
```

#### 第三层：接口权限
```
┌─────────────────────────────────────────┐
│ 接口权限 (permission_type=3) ⭐        │
├─────────────────────────────────────────┤
│ • 权限编码: api:advertiser:create       │
│ • 控制内容: 后端API访问权限              │
│ • 校验位置: ApiPermissionAspect (AOP)  │
│ • 数据来源: sys_permission表            │
│ • 示例:                                │
│   - api:advertiser:list (列表接口)      │
│   - api:advertiser:create (创建接口)    │
│   - api:advertiser:update (更新接口)    │
│   - api:advertiser:delete (删除接口)    │
└─────────────────────────────────────────┘
```

### 1.3 完整的权限树示例

```
advertiser (type=1, 菜单权限)
├─ advertiser:create (type=2, 按钮权限)
│  └─ api:advertiser:create (type=3, 接口权限)
├─ advertiser:update (type=2, 按钮权限)
│  └─ api:advertiser:update (type=3, 接口权限)
├─ advertiser:delete (type=2, 按钮权限)
│  └─ api:advertiser:delete (type=3, 接口权限)
├─ advertiser:query (type=2, 按钮权限)
│  ├─ api:advertiser:list (type=3, 接口权限)
│  └─ api:advertiser:detail (type=3, 接口权限)
└─ advertiser:audit (type=2, 按钮权限)
   └─ api:advertiser:audit (type=3, 接口权限)
```

---

## 二、权限设计理念

### 2.1 核心原则

#### 原则1：关注点分离 (Separation of Concerns)
```
菜单权限   → 关注"能否看到页面"
按钮权限   → 关注"能否看到操作"
接口权限   → 关注"能否调用接口"

三层权限各司其职，互不干扰
```

#### 原则2：最小权限原则 (Least Privilege)
```
默认配置：只给用户必需的最小权限
扩展方式：根据业务需要逐步增加权限
定期审查：及时回收不再需要的权限
```

#### 原则3：防御深度 (Defense in Depth)
```
第一道防线：前端路由守卫（菜单权限）
第二道防线：前端按钮显示（按钮权限）
第三道防线：后端API校验（接口权限）← 最终防线

即使前端被绕过，后端仍能保护系统安全
```

### 2.2 为什么需要三层权限？

#### 对比：单一权限 vs 三层权限

| 场景 | 单一权限方案 | 三层权限方案 |
|------|-------------|-------------|
| **用户体验** | ❌ 无法区分"能看到"和"能操作" | ✅ 菜单、按钮、API分层控制 |
| **安全保护** | ⚠️ 仅后端校验 | ✅ 前后端多重防护 |
| **灵活性** | ❌ 无法实现"试用"场景 | ✅ 支持复杂的业务场景 |
| **可维护性** | ❌ 权限耦合在一起 | ✅ 每层职责清晰 |
| **扩展性** | ❌ 难以添加新的控制维度 | ✅ 易于扩展新权限类型 |

#### 实际业务需求示例

**需求1：试用账号**
```
✅ 用户能看到"新增"按钮（引导升级）
❌ 调用API时提示"权限不足，请升级"

实现：
- 分配按钮权限: advertiser:create
- 不分配接口权限: api:advertiser:create
```

**需求2：达到配额限制**
```
✅ 用户能看到"创建"按钮
✅ 按钮置灰（达到配额上限）
❌ 调用API时返回"已达到创建上限"

实现：
- 前端判断业务规则，按钮置灰
- 后端返回配额不足的错误提示
```

**需求3：分级权限**
```
普通用户：查看权限
管理员用户：查看 + 编辑权限
超级管理员：查看 + 编辑 + 删除权限

实现：
- 通过角色分配不同的按钮权限和接口权限
```

---

## 三、数据库设计

### 3.1 权限表结构 (sys_permission)

```sql
CREATE TABLE `sys_permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父权限ID，0表示根节点',
    `permission_code` VARCHAR(100) NOT NULL COMMENT '权限编码（唯一标识）',
    `permission_name` VARCHAR(50) NOT NULL COMMENT '权限名称',
    `permission_type` TINYINT NOT NULL COMMENT '权限类型：1-路由 2-按钮 3-接口',
    `path` VARCHAR(200) DEFAULT NULL COMMENT '路由路径/接口路径',
    `method` VARCHAR(10) DEFAULT NULL COMMENT 'HTTP方法：GET/POST/PUT/DELETE等',
    `icon` VARCHAR(50) DEFAULT NULL COMMENT '图标（用于菜单展示）',
    `sort_order` INT DEFAULT 0 COMMENT '排序号',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_permission_code` (`permission_code`),
    KEY `idx_permission_type` (`permission_type`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统权限表';
```

**字段说明**：
- `permission_type`: 权限类型，1=菜单，2=按钮，3=接口
- `permission_code`: 权限编码，全局唯一
  - 菜单权限: `advertiser`, `system:user` 等
  - 按钮权限: `advertiser:create`, `advertiser:update` 等
  - 接口权限: `api:advertiser:create`, `api:advertiser:list` 等
- `parent_id`: 父权限ID，0表示根节点，实现树形结构
- `path`: 路由路径或接口路径
- `method`: HTTP方法，仅接口权限使用

### 3.2 菜单表结构 (sys_menu)

```sql
CREATE TABLE `sys_menu` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父菜单ID（0表示根菜单）',
    `menu_name` VARCHAR(50) NOT NULL COMMENT '菜单名称',
    `menu_type` TINYINT NOT NULL COMMENT '菜单类型：1-目录 2-菜单 3-按钮',
    `icon` VARCHAR(50) DEFAULT NULL COMMENT '菜单图标',
    `path` VARCHAR(200) DEFAULT NULL COMMENT '路由路径',
    `component` VARCHAR(200) DEFAULT NULL COMMENT '组件路径',
    `permission_code` VARCHAR(100) DEFAULT NULL COMMENT '权限编码（关联sys_permission）',
    `sort_order` INT DEFAULT 0 COMMENT '排序号',
    `visible` TINYINT NOT NULL DEFAULT 1 COMMENT '是否显示：0-隐藏，1-显示',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_permission_code` (`permission_code`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统菜单表';
```

**字段说明**：
- `menu_type`: 菜单类型，1=目录，2=菜单，3=按钮
- `permission_code`: 关联的权限编码，指向 `sys_permission.permission_code`
- `visible`: 控制菜单是否显示

### 3.3 角色权限关联表 (sys_role_permission)

```sql
CREATE TABLE `sys_role_permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT NOT NULL COMMENT '权限ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';
```

### 3.4 用户角色关联表 (sys_user_role)

```sql
CREATE TABLE `sys_user_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';
```

### 3.5 初始化数据示例

```sql
-- 广告主管理模块完整权限配置
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_code`, `permission_name`, `permission_type`, `path`, `method`, `sort_order`, `status`) VALUES
-- 第一层：菜单权限
(1, 0, 'advertiser', '广告主管理', 1, '/advertisers', NULL, 10, 1),

-- 第二层：按钮权限
(2, 1, 'advertiser:create', '新增广告主', 2, NULL, 'POST', 1, 1),
(3, 1, 'advertiser:update', '编辑广告主', 2, NULL, 'PUT', 2, 1),
(4, 1, 'advertiser:delete', '删除广告主', 2, NULL, 'DELETE', 3, 1),
(5, 1, 'advertiser:query', '查看广告主', 2, NULL, 'GET', 4, 1),
(6, 1, 'advertiser:audit', '审核广告主', 2, NULL, 'PUT', 5, 1),

-- 第三层：接口权限
(7, 1, 'api:advertiser:list', '广告主列表接口', 3, '/api/advertisers', 'GET', 10, 1),
(8, 1, 'api:advertiser:create', '创建广告主接口', 3, '/api/advertisers', 'POST', 11, 1),
(9, 1, 'api:advertiser:update', '更新广告主接口', 3, '/api/advertisers/*', 'PUT', 12, 1),
(10, 1, 'api:advertiser:delete', '删除广告主接口', 3, '/api/advertisers/*', 'DELETE', 13, 1),
(11, 1, 'api:advertiser:detail', '广告主详情接口', 3, '/api/advertisers/*', 'GET', 14, 1),
(12, 1, 'api:advertiser:audit', '审核广告主接口', 3, '/api/advertisers/*/audit', 'PUT', 15, 1);

-- 系统管理模块权限配置
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_code`, `permission_name`, `permission_type`, `path`, `method`, `sort_order`, `status`) VALUES
(100, 0, 'system', '系统管理', 1, '/system', NULL, 100, 1),
(101, 100, 'system:user', '用户管理', 1, '/system/users', NULL, 1, 1),
(102, 100, 'system:role', '角色管理', 1, '/system/roles', NULL, 2, 1),
(103, 100, 'system:permission', '权限管理', 1, '/system/permissions', NULL, 3, 1),
(104, 100, 'system:menu', '菜单管理', 1, '/system/menus', NULL, 4, 1);

-- 系统管理模块的按钮权限
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_code`, `permission_name`, `permission_type`, `path`, `method`, `sort_order`, `status`) VALUES
(201, 101, 'system:user:create', '新增用户', 2, NULL, 'POST', 1, 1),
(202, 101, 'system:user:update', '编辑用户', 2, NULL, 'PUT', 2, 1),
(203, 101, 'system:user:delete', '删除用户', 2, NULL, 'DELETE', 3, 1),
(204, 101, 'system:user:query', '查看用户', 2, NULL, 'GET', 4, 1),
(205, 101, 'system:user:reset-password', '重置密码', 2, NULL, 'PUT', 5, 1);
```

---

## 四、后端实现

### 4.1 注解定义

#### @RequiresPermission - 按钮权限注解

```java
package wake.su.zhuque.common.security.annotation;

import java.lang.annotation.*;

/**
 * 权限校验注解（第二层：按钮权限）
 * 用于方法级别的权限控制
 *
 * @author wake.su
 * @since 2026-01-09
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPermission {

    /**
     * 需要的权限编码
     */
    String[] value() default {};

    /**
     * 逻辑类型：AND/OR
     */
    LogicalType logical() default LogicalType.OR;

    enum LogicalType {
        AND,  // 需要拥有所有权限
        OR    // 拥有任一权限即可
    }
}
```

#### @RequiresApiPermission - 接口权限注解

```java
package wake.su.zhuque.common.security.annotation;

import java.lang.annotation.*;

/**
 * API接口权限校验注解（第三层：接口权限）
 * 专门用于Controller层的API接口权限控制
 *
 * @author wake.su
 * @since 2026-01-11
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresApiPermission {

    /**
     * 需要的API权限编码
     */
    String[] value() default {};

    /**
     * 逻辑类型：AND/OR
     */
    LogicalType logical() default LogicalType.OR;

    enum LogicalType {
        AND,  // 需要拥有所有权限
        OR    // 拥有任一权限即可
    }
}
```

### 4.2 AOP切面实现

#### PermissionAspect - 按钮权限切面

```java
package wake.su.zhuque.common.security.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import wake.su.zhuque.common.core.exception.BusinessException;
import wake.su.zhuque.common.security.annotation.RequiresPermission;
import wake.su.zhuque.common.security.validator.PermissionValidator;
import wake.su.zhuque.common.util.JwtUtil;

import java.util.Arrays;
import java.util.List;

/**
 * 权限校验切面（第二层：按钮权限）
 *
 * @author wake.su
 * @since 2026-01-09
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    private final JwtUtil jwtUtil;

    @Lazy
    private final PermissionValidator permissionValidator;

    @Before("@annotation(requiresPermission)")
    public void checkPermission(JoinPoint joinPoint, RequiresPermission requiresPermission) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("未登录或登录已过期");
        }

        String[] permissionCodes = requiresPermission.value();
        if (permissionCodes.length == 0) {
            return;
        }

        List<String> permissionList = Arrays.asList(permissionCodes);
        RequiresPermission.LogicalType logicalType = requiresPermission.logical();

        boolean hasPermission = permissionValidator.hasPermissions(
            userId,
            permissionList,
            logicalType == RequiresPermission.LogicalType.AND
        );

        if (!hasPermission) {
            throw new BusinessException("权限不足，需要权限：" + String.join(" 或 ", permissionList));
        }
    }

    private Long getCurrentUserId() {
        // 实现略
        return null;
    }
}
```

#### ApiPermissionAspect - 接口权限切面

```java
package wake.su.zhuque.common.security.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import wake.su.zhuque.common.core.exception.BusinessException;
import wake.su.zhuque.common.security.annotation.RequiresApiPermission;
import wake.su.zhuque.common.security.validator.PermissionValidator;
import wake.su.zhuque.common.util.JwtUtil;

import java.util.Arrays;
import java.util.List;

/**
 * API接口权限校验切面（第三层：接口权限）
 *
 * @author wake.su
 * @since 2026-01-11
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ApiPermissionAspect {

    private final JwtUtil jwtUtil;

    @Lazy
    private final PermissionValidator permissionValidator;

    @Before("@annotation(requiresApiPermission)")
    public void checkApiPermission(JoinPoint joinPoint, RequiresApiPermission requiresApiPermission) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("未登录或登录已过期");
        }

        String[] permissionCodes = requiresApiPermission.value();
        if (permissionCodes.length == 0) {
            return;
        }

        List<String> permissionList = Arrays.asList(permissionCodes);
        RequiresApiPermission.LogicalType logicalType = requiresApiPermission.logical();

        boolean hasPermission = permissionValidator.hasPermissions(
            userId,
            permissionList,
            logicalType == RequiresApiPermission.LogicalType.AND
        );

        if (!hasPermission) {
            log.warn("API权限不足: userId={}, requiredApiPermissions={}", userId, permissionList);
            throw new BusinessException("API权限不足，需要接口权限：" + String.join(" 或 ", permissionList));
        }
    }

    private Long getCurrentUserId() {
        // 实现略
        return null;
    }
}
```

### 4.3 Controller使用示例

```java
@RestController
@RequestMapping("/api/advertisers")
public class AdvertiserController {

    /**
     * 创建广告主
     *
     * 权限说明：
     * - 前端按钮：需要 advertiser:create 权限（第二层）
     * - 后端API：需要 api:advertiser:create 权限（第三层）
     */
    @PostMapping
    @RequiresApiPermission("api:advertiser:create")
    public Result<String> createAdvertiser(@RequestBody CreateRequest request) {
        return Result.success("创建广告主成功");
    }

    /**
     * 广告主列表
     */
    @GetMapping
    @RequiresApiPermission("api:advertiser:list")
    public Result<List<AdvertiserVO>> listAdvertisers() {
        return Result.success(advertiserList);
    }

    /**
     * 广告主详情
     */
    @GetMapping("/{id}")
    @RequiresApiPermission("api:advertiser:detail")
    public Result<AdvertiserVO> getAdvertiser(@PathVariable Long id) {
        return Result.success(advertiser);
    }

    /**
     * 更新广告主
     */
    @PutMapping("/{id}")
    @RequiresApiPermission("api:advertiser:update")
    public Result<String> updateAdvertiser(@PathVariable Long id, @RequestBody UpdateRequest request) {
        return Result.success("更新广告主成功");
    }

    /**
     * 删除广告主
     */
    @DeleteMapping("/{id}")
    @RequiresApiPermission("api:advertiser:delete")
    public Result<String> deleteAdvertiser(@PathVariable Long id) {
        return Result.success("删除广告主成功");
    }

    /**
     * 示例：OR逻辑（拥有任一权限即可）
     */
    @PostMapping("/batch")
    @RequiresApiPermission(value = {"api:advertiser:create", "api:advertiser:audit"},
                              logical = RequiresApiPermission.LogicalType.OR)
    public Result<String> batchOperation() {
        return Result.success("批量操作成功");
    }

    /**
     * 示例：AND逻辑（必须同时拥有所有权限）
     */
    @PostMapping("/special")
    @RequiresApiPermission(value = {"api:advertiser:create", "api:advertiser:audit"},
                              logical = RequiresApiPermission.LogicalType.AND)
    public Result<String> specialOperation() {
        return Result.success("特殊操作成功");
    }
}
```

---

## 五、前端实现

### 5.1 权限判断工具函数

```javascript
// src/utils/permission.js

/**
 * 检查用户是否拥有指定权限
 * @param {string} permissionCode - 权限编码
 * @returns {boolean}
 */
export function hasPermission(permissionCode) {
  const permissions = store.state.user.permissions || []
  return permissions.includes(permissionCode)
}

/**
 * 检查用户是否拥有所有指定权限（AND逻辑）
 * @param {string[]} permissionCodes - 权限编码数组
 * @returns {boolean}
 */
export function hasAllPermissions(permissionCodes) {
  if (!Array.isArray(permissionCodes)) {
    return false
  }
  const permissions = store.state.user.permissions || []
  return permissionCodes.every(code => permissions.includes(code))
}

/**
 * 检查用户是否拥有任一指定权限（OR逻辑）
 * @param {string[]} permissionCodes - 权限编码数组
 * @returns {boolean}
 */
export function hasAnyPermission(permissionCodes) {
  if (!Array.isArray(permissionCodes)) {
    return false
  }
  const permissions = store.state.user.permissions || []
  return permissionCodes.some(code => permissions.includes(code))
}
```

### 5.2 Vue组件使用示例

```vue
<template>
  <div class="advertiser-page">
    <!--
      第一层权限：菜单权限
      在路由配置中设置 meta.permission
    -->

    <!--
      第二层权限：按钮权限
      使用v-if控制按钮显示
    -->
    <el-button
      v-if="hasPermission('advertiser:create')"
      type="primary"
      @click="handleCreate"
    >
      新增广告主
    </el-button>

    <el-button
      v-if="hasPermission('advertiser:update')"
      @click="handleEdit"
    >
      编辑
    </el-button>

    <el-button
      v-if="hasPermission('advertiser:delete')"
      type="danger"
      @click="handleDelete"
    >
      删除
    </el-button>

    <!-- 数据表格 -->
    <el-table :data="tableData">
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button
            v-if="hasPermission('advertiser:update')"
            size="small"
            @click="editRow(row)"
          >
            编辑
          </el-button>

          <el-button
            v-if="hasPermission('advertiser:delete')"
            size="small"
            type="danger"
            @click="deleteRow(row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { hasPermission } from '@/utils/permission'
import { createAdvertiser, updateAdvertiser, deleteAdvertiser } from '@/api/advertiser'

const tableData = ref([])

// 第三层权限：接口权限在调用API时自动校验
// 后端的@ApiPermission注解会拦截请求并校验权限

const handleCreate = async () => {
  try {
    // 调用API，后端会校验 api:advertiser:create 权限
    const res = await createAdvertiser(formData)
    ElMessage.success('创建成功')
    loadData()
  } catch (error) {
    if (error.code === 403) {
      ElMessage.error('API权限不足，请联系管理员')
    } else {
      ElMessage.error(error.message || '创建失败')
    }
  }
}

const handleEdit = async (row) => {
  try {
    // 后端会校验 api:advertiser:update 权限
    const res = await updateAdvertiser(row.id, formData)
    ElMessage.success('更新成功')
    loadData()
  } catch (error) {
    if (error.code === 403) {
      ElMessage.error('API权限不足，请联系管理员')
    }
  }
}

const handleDelete = async (row) => {
  try {
    // 后端会校验 api:advertiser:delete 权限
    const res = await deleteAdvertiser(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error.code === 403) {
      ElMessage.error('API权限不足，请联系管理员')
    }
  }
}

onMounted(() => {
  loadData()
})

const loadData = async () => {
  try {
    // 后端会校验 api:advertiser:list 权限
    const res = await getAdvertiserList()
    tableData.value = res.data
  } catch (error) {
    if (error.code === 403) {
      ElMessage.error('API权限不足，请联系管理员')
    }
  }
}
</script>
```

### 5.3 路由守卫配置

```javascript
// src/router/index.js

import { createRouter, createWebHistory } from 'vue-router'
import { hasPermission } from '@/utils/permission'
import store from '@/store'

const routes = [
  {
    path: '/advertisers',
    name: 'AdvertiserList',
    component: () => import('@/views/advertiser/list.vue'),
    meta: {
      title: '广告主管理',
      // 第一层权限：菜单权限
      permission: 'advertiser'
    }
  },
  {
    path: '/system/users',
    name: 'UserList',
    component: () => import('@/views/system/user/list.vue'),
    meta: {
      title: '用户管理',
      permission: 'system:user'
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局路由守卫
router.beforeEach((to, from, next) => {
  // 检查路由是否需要权限
  if (to.meta.permission) {
    // 检查用户是否拥有该权限
    if (hasPermission(to.meta.permission)) {
      next()
    } else {
      // 无权限，跳转到无权限页面
      next({ name: 'Forbidden' })
    }
  } else {
    // 不需要权限，直接放行
    next()
  }
})

export default router
```

### 5.4 Vuex Store配置

```javascript
// src/store/modules/user.js

import { login, logout, getUserInfo } from '@/api/auth'

const state = {
  token: '',
  userInfo: {},
  permissions: [] // 用户的所有权限（菜单+按钮+接口）
}

const mutations = {
  SET_TOKEN(state, token) {
    state.token = token
  },
  SET_USER_INFO(state, userInfo) {
    state.userInfo = userInfo
  },
  SET_PERMISSIONS(state, permissions) {
    state.permissions = permissions
  }
}

const actions = {
  // 登录
  async login({ commit }, loginForm) {
    const res = await login(loginForm)
    commit('SET_TOKEN', res.data.token)
    return res
  },

  // 获取用户信息（包含权限）
  async getUserInfo({ commit }) {
    const res = await getUserInfo()
    commit('SET_USER_INFO', res.data.userInfo)
    commit('SET_PERMISSIONS', res.data.permissions)
    return res
  },

  // 登出
  async logout({ commit }) {
    await logout()
    commit('SET_TOKEN', '')
    commit('SET_USER_INFO', {})
    commit('SET_PERMISSIONS', [])
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
```

---

## 六、权限配置策略

### 6.1 角色权限矩阵

| 角色 | 菜单权限 | 按钮权限 | 接口权限 | 说明 |
|------|---------|---------|---------|------|
| **超级管理员** | 全部 | 全部 | 全部 | 拥有系统所有权限 |
| **管理员** | 全部 | 查看+编辑 | 查看+编辑 | 可以查看和编辑，不能删除 |
| **运营人员** | 广告主 | 查看 | 查看 | 只读权限 |
| **广告主** | 广告主 | 查看 | 查看 | 只能查看自己的数据 |
| **试用用户** | 广告主 | 全部 | 无 | 可以看到按钮，但API无权限 |

### 6.2 标准权限配置

#### 场景1：完整权限（管理员）

```sql
-- 为管理员角色分配完整权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(1, 1),   -- advertiser (菜单)
(1, 2), (1, 3), (1, 4), (1, 5), (1, 6),  -- 所有按钮
(1, 7), (1, 8), (1, 9), (1, 10), (1, 11), (1, 12);  -- 所有接口

-- 效果：
-- ✅ 能看到"广告主管理"菜单
-- ✅ 能看到所有操作按钮
-- ✅ 能调用所有API接口
```

#### 场景2：只读权限（运营人员）

```sql
-- 为运营人员角色分配只读权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(2, 1),   -- advertiser (菜单)
(2, 5),   -- advertiser:query (按钮)
(2, 7), (2, 11);  -- api:advertiser:list, api:advertiser:detail (接口)

-- 效果：
-- ✅ 能看到"广告主管理"菜单
-- ✅ 能看到"查看"按钮
-- ❌ 看不到"新增"、"编辑"、"删除"按钮
-- ✅ 能调用列表和详情接口
-- ❌ 调用其他接口时返回"API权限不足"
```

#### 场景3：试用用户权限

```sql
-- 为试用用户角色分配特殊权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(3, 1),   -- advertiser (菜单)
(3, 2), (3, 3), (3, 4), (3, 5),  -- 所有按钮（引导升级）
-- 不分配任何接口权限

-- 效果：
-- ✅ 能看到"广告主管理"菜单
-- ✅ 能看到所有操作按钮（吸引用户）
-- ❌ 调用任何API时返回"API权限不足，请升级账号"
```

### 6.3 特殊权限配置

#### 配置1：按钮可见但API无权限（试用场景）

```
用户权限：
├─ advertiser (菜单) ✅
├─ advertiser:create (按钮) ✅
└─ api:advertiser:create (接口) ❌

用户操作：
1. 能看到"广告主管理"菜单
2. 能看到"新增"按钮
3. 点击按钮调用API
4. 后端返回"API权限不足，请升级账号"
```

#### 配置2：按钮置灰（配额限制）

```
权限配置：
├─ advertiser (菜单) ✅
├─ advertiser:create (按钮) ✅
└─ api:advertiser:create (接口) ✅

前端实现：
1. 查询用户配额
2. if (已用配额 >= 总配额) {
     按钮置灰 :disabled="true"
     提示："已达到创建上限"
   }

后端实现：
1. 校验API权限
2. 检查配额是否用完
3. if (已用配额 >= 总配额) {
     返回错误："已达到创建上限"
   }
```

#### 配置3：数据权限（只能查看自己的数据）

```
权限配置：
├─ advertiser (菜单) ✅
├─ advertiser:query (按钮) ✅
└─ api:advertiser:list (接口) ✅

后端实现：
1. 校验API权限（通过）
2. 查询当前用户所属的角色
3. if (角色 == '广告主') {
     只返回该用户创建的广告主数据
     WHERE created_by = current_user_id
   } else if (角色 == '管理员') {
     返回所有广告主数据
   }
```

---

## 七、实际应用场景

### 7.1 场景：试用账号转正式账号

#### 业务需求
- 试用账号：能看到所有功能，但调用API时提示升级
- 正式账号：正常使用所有功能

#### 实现方案

**阶段1：试用账号**
```sql
-- 分配菜单和按钮权限，不分配接口权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(1, 1),   -- advertiser (菜单)
(1, 2), (1, 3), (1, 4), (1, 5);  -- 所有按钮
-- 不分配接口权限
```

**阶段2：升级为正式账号**
```sql
-- 添加接口权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(1, 7), (1, 8), (1, 9), (1, 10), (1, 11);  -- 所有接口
```

**前端处理**
```javascript
// API调用失败时的处理
try {
  await createAdvertiser(data)
} catch (error) {
  if (error.code === 403) {
    // API权限不足，提示升级
    ElMessageBox.confirm(
      '您的试用账号已到期，升级为正式账号即可使用此功能',
      '权限提示',
      {
        confirmButtonText: '立即升级',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(() => {
      // 跳转到升级页面
      router.push({ name: 'Upgrade' })
    })
  }
}
```

### 7.2 场景：达到使用配额上限

#### 业务需求
- 用户创建广告主的数量有上限
- 达到上限时，按钮置灰
- 尝试调用API时返回友好提示

#### 实现方案

**前端实现**
```vue
<template>
  <div>
    <el-button
      v-if="hasPermission('advertiser:create')"
      :disabled="!canCreate"
      @click="handleCreate"
    >
      新增广告主
      <span v-if="!canCreate">（已达到上限 {{ advertiserCount }}/{{ maxAdvertisers }}）</span>
    </el-button>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const advertiserCount = ref(0)
const maxAdvertisers = ref(10)

const canCreate = computed(() => {
  return advertiserCount.value < maxAdvertisers.value
})

onMounted(async () => {
  // 获取用户配额信息
  const res = await getUserQuota()
  advertiserCount.value = res.data.advertiserCount
  maxAdvertisers.value = res.data.maxAdvertisers
})

const handleCreate = async () => {
  if (!canCreate.value) {
    ElMessage.warning('已达到创建上限，请联系客服提升配额')
    return
  }
  // 调用创建API
}
</script>
```

**后端实现**
```java
@PostMapping
@RequiresApiPermission("api:advertiser:create")
public Result<String> createAdvertiser(@RequestBody CreateRequest request) {
    // 1. 校验API权限（已通过AOP切面校验）

    // 2. 校验配额
    Long userId = getCurrentUserId();
    int currentCount = advertiserService.countByUserId(userId);
    int maxCount = userQuotaService.getMaxAdvertisers(userId);

    if (currentCount >= maxCount) {
        throw new BusinessException("已达到创建上限（" + currentCount + "/" + maxCount + "），请联系客服提升配额");
    }

    // 3. 创建广告主
    advertiserService.create(request);
    return Result.success("创建成功");
}
```

### 7.3 场景：审批流程权限

#### 业务需求
- 普通用户：创建广告主（需要审核）
- 审核员：审核广告主
- 管理员：直接通过，无需审核

#### 实现方案

**数据库配置**
```sql
-- 普通用户角色
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(1, 1),   -- advertiser (菜单)
(1, 2), (1, 5),  -- advertiser:create, advertiser:query
(1, 7), (1, 11);  -- api:advertiser:list, api:advertiser:detail

-- 审核员角色
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(2, 1),   -- advertiser (菜单)
(2, 6),   -- advertiser:audit (按钮)
(2, 12);  -- api:advertiser:audit (接口)

-- 管理员角色
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(3, 1),   -- advertiser (菜单)
(3, 2), (3, 4), (3, 5),  -- 所有按钮
(3, 7), (3, 8), (3, 9), (3, 10), (3, 11);  -- 所有接口
```

**前端实现**
```vue
<template>
  <div>
    <!-- 普通用户：只能看到创建按钮 -->
    <el-button
      v-if="isOrdinaryUser && hasPermission('advertiser:create')"
      @click="handleCreate"
    >
      创建广告主（需要审核）
    </el-button>

    <!-- 审核员：能看到审核按钮 -->
    <el-button
      v-if="isAuditor && hasPermission('advertiser:audit')"
      type="warning"
      @click="handleAudit"
    >
      审核
    </el-button>

    <!-- 管理员：能看到直接通过按钮 -->
    <el-button
      v-if="isAdmin && hasPermission('advertiser:create')"
      type="success"
      @click="handleDirectApprove"
    >
      直接通过
    </el-button>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useStore } from 'vuex'

const store = useStore()

const isOrdinaryUser = computed(() => {
  return store.getters.roles.includes('ORDINARY_USER')
})

const isAuditor = computed(() => {
  return store.getters.roles.includes('AUDITOR')
})

const isAdmin = computed(() => {
  return store.getters.roles.includes('ADMIN')
})
</script>
```

---

## 八、权限继承机制

### 8.1 菜单权限向上继承（推荐实现）

#### 设计理念
```
拥有子权限 → 自动拥有父权限

原因：
1. 解决"有子菜单看不到父菜单"的问题
2. 符合用户认知习惯
3. 前端菜单树需要父节点才能展开
```

#### 实现方式

```java
/**
 * 获取用户权限（含父权限）
 */
public List<String> getUserPermissionCodes(Long userId) {
    // 1. 获取直接权限
    Set<String> directPermissions = getDirectPermissions(userId);

    // 2. 为菜单权限添加父权限
    Set<String> allPermissions = new HashSet<>(directPermissions);
    for (String code : directPermissions) {
        if (isMenuPermission(code)) {
            allPermissions.addAll(getParentPermissionCodes(code));
        }
    }

    return new ArrayList<>(allPermissions);
}

/**
 * 判断是否为菜单权限
 */
private boolean isMenuPermission(String permissionCode) {
    SysPermissionDO permission = permissionMapper.selectOne(
        new LambdaQueryWrapper<SysPermissionDO>()
            .eq(SysPermissionDO::getPermissionCode, permissionCode)
    );
    return permission != null && permission.getPermissionType() == 1;
}

/**
 * 递归查找父权限
 */
private Set<String> getParentPermissionCodes(String permissionCode) {
    Set<String> parents = new HashSet<>();
    SysPermissionDO permission = getByCode(permissionCode);

    if (permission != null && permission.getParentId() != 0) {
        SysPermissionDO parent = permissionMapper.selectById(permission.getParentId());
        if (parent != null) {
            parents.add(parent.getPermissionCode());
            // 递归查找更上级的父权限
            parents.addAll(getParentPermissionCodes(parent.getPermissionCode()));
        }
    }

    return parents;
}
```

### 8.2 按钮和接口权限不继承

#### 设计理念
```
按钮权限和接口权限：精确匹配，不继承

原因：
1. 安全考虑：避免权限过度开放
2. 明确性：权限配置清晰明了
3. 灵活性：可以精细控制每个操作
```

#### 示例说明

```
用户权限：
└─ advertiser:create (按钮权限)

效果：
✅ 能看到"新增"按钮（有按钮权限）
❌ 看不到"编辑"、"删除"按钮（无按钮权限）
✅ 能调用"创建"接口（有按钮权限时，通常也分配对应接口权限）
❌ 不能调用"编辑"、"删除"接口（无接口权限）
```

### 8.3 继承关系总结

| 权限类型 | 是否继承 | 继承方向 | 原因 |
|---------|---------|---------|------|
| 菜单权限 | ✅ 是 | 向上继承 | 解决"有子菜单看不到父菜单"的问题 |
| 按钮权限 | ❌ 否 | 无 | 精确控制，避免权限过度开放 |
| 接口权限 | ❌ 否 | 无 | 安全考虑，必须精确匹配 |

---

## 九、性能优化

### 9.1 Redis缓存策略

#### 缓存设计
```java
/**
 * 用户权限缓存服务
 */
@Service
@RequiredArgsConstructor
public class PermissionCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String USER_PERMISSIONS_KEY = "user:permissions:";
    private static final long CACHE_EXPIRE_SECONDS = 1800; // 30分钟

    /**
     * 缓存用户权限
     */
    public void cacheUserPermissions(Long userId, Set<String> permissions) {
        String key = USER_PERMISSIONS_KEY + userId;
        redisTemplate.opsForValue().set(key, permissions, CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * 获取缓存的用户权限
     */
    public Set<String> getUserPermissions(Long userId) {
        String key = USER_PERMISSIONS_KEY + userId;
        return (Set<String>) redisTemplate.opsForValue().get(key);
    }

    /**
     * 清除用户权限缓存
     */
    public void clearUserPermissions(Long userId) {
        String key = USER_PERMISSIONS_KEY + userId;
        redisTemplate.delete(key);
    }

    /**
     * 清除角色的所有用户权限缓存
     */
    public void clearRolePermissions(Long roleId) {
        // 查询拥有该角色的所有用户
        List<Long> userIds = getUserIdsByRole(roleId);

        // 批量删除缓存
        List<String> keys = userIds.stream()
            .map(userId -> USER_PERMISSIONS_KEY + userId)
            .collect(Collectors.toList());

        redisTemplate.delete(keys);
    }
}
```

#### 权限查询流程
```java
@Override
public List<String> getUserPermissionCodes(Long userId) {
    // 1. 先从Redis缓存获取
    Set<String> cachedPermissions = permissionCacheService.getUserPermissions(userId);
    if (cachedPermissions != null) {
        log.debug("从缓存获取用户权限: userId={}, permissionsCount={}", userId, cachedPermissions.size());
        return new ArrayList<>(cachedPermissions);
    }

    // 2. 缓存未命中，从数据库查询
    log.debug("缓存未命中，从数据库查询用户权限: userId={}", userId);

    // 查询用户的所有角色
    List<SysUserRoleDO> userRoles = userRoleMapper.selectList(
        new LambdaQueryWrapper<SysUserRoleDO>()
            .eq(SysUserRoleDO::getUserId, userId)
    );

    if (userRoles.isEmpty()) {
        // 缓存空结果，避免频繁查询
        permissionCacheService.cacheUserPermissions(userId, Set.of());
        return new ArrayList<>();
    }

    List<Long> roleIds = userRoles.stream()
        .map(SysUserRoleDO::getRoleId)
        .collect(Collectors.toList());

    // 查询角色的所有权限
    List<SysRolePermissionDO> rolePermissions = rolePermissionMapper.selectList(
        new LambdaQueryWrapper<SysRolePermissionDO>()
            .in(SysRolePermissionDO::getRoleId, roleIds)
    );

    if (rolePermissions.isEmpty()) {
        permissionCacheService.cacheUserPermissions(userId, Set.of());
        return new ArrayList<>();
    }

    List<Long> permissionIds = rolePermissions.stream()
        .map(SysRolePermissionDO::getPermissionId)
        .distinct()
        .collect(Collectors.toList());

    // 查询权限详情
    List<SysPermissionDO> permissions = permissionMapper.selectList(
        new LambdaQueryWrapper<SysPermissionDO>()
            .in(SysPermissionDO::getId, permissionIds)
            .eq(SysPermissionDO::getStatus, 1)
    );

    // 转换为Set并缓存
    Set<String> permissionCodes = permissions.stream()
        .map(SysPermissionDO::getPermissionCode)
        .collect(Collectors.toSet());

    // 缓存到Redis
    permissionCacheService.cacheUserPermissions(userId, permissionCodes);
    log.info("缓存用户权限: userId={}, permissionsCount={}", userId, permissionCodes.size());

    return new ArrayList<>(permissionCodes);
}
```

### 9.2 缓存更新策略

#### 场景1：角色权限变更时清除缓存

```java
@Override
@Transactional(rollbackFor = Exception.class)
public void assignPermissions(AssignPermissionsRequest request) {
    // 1. 删除角色的所有权限
    rolePermissionMapper.delete(
        new LambdaQueryWrapper<SysRolePermissionDO>()
            .eq(SysRolePermissionDO::getRoleId, request.getRoleId())
    );

    // 2. 分配新权限
    if (!request.getPermissionIds().isEmpty()) {
        List<SysRolePermissionDO> list = request.getPermissionIds().stream()
            .map(permissionId -> {
                SysRolePermissionDO rp = new SysRolePermissionDO();
                rp.setRoleId(request.getRoleId());
                rp.setPermissionId(permissionId);
                return rp;
            })
            .collect(Collectors.toList());

        list.forEach(rolePermissionMapper::insert);
    }

    // 3. 清除所有拥有该角色的用户权限缓存
    permissionCacheService.clearRolePermissions(request.getRoleId());

    log.info("为角色{}分配权限成功，共{}个", request.getRoleId(), request.getPermissionIds().size());
}
```

#### 场景2：用户角色变更时清除缓存

```java
@Override
@Transactional(rollbackFor = Exception.class)
public void assignRolesToUser(Long userId, List<Long> roleIds) {
    // 1. 删除用户的所有角色
    userRoleMapper.delete(
        new LambdaQueryWrapper<SysUserRoleDO>()
            .eq(SysUserRoleDO::getUserId, userId)
    );

    // 2. 分配新角色
    if (!roleIds.isEmpty()) {
        List<SysUserRoleDO> list = roleIds.stream()
            .map(roleId -> {
                SysUserRoleDO ur = new SysUserRoleDO();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                return ur;
            })
            .collect(Collectors.toList());

        list.forEach(userRoleMapper::insert);
    }

    // 3. 清除用户权限缓存
    permissionCacheService.clearUserPermissions(userId);

    log.info("为用户{}分配角色成功，共{}个", userId, roleIds.size());
}
```

### 9.3 数据库索引优化

```sql
-- 权限表索引
CREATE INDEX idx_permission_type_status ON sys_permission(permission_type, status);
CREATE INDEX idx_parent_id ON sys_permission(parent_id);

-- 角色权限关联表索引
CREATE INDEX idx_role_id ON sys_role_permission(role_id);
CREATE INDEX idx_permission_id ON sys_role_permission(permission_id);

-- 用户角色关联表索引
CREATE INDEX idx_user_id ON sys_user_role(user_id);
CREATE INDEX idx_role_id ON sys_user_role(role_id);

-- 菜单表索引
CREATE INDEX idx_permission_code ON sys_menu(permission_code);
CREATE INDEX idx_parent_id_status ON sys_menu(parent_id, status);
```

---

## 十、常见问题

### Q1: 前端和后端权限不一致怎么办？

**A:** 这通常是缓存问题，解决方法：

1. **清除Redis缓存**
```bash
redis-cli KEYS "user:permissions:*" | xargs redis-cli DEL
```

2. **后端提供清除缓存接口**
```java
@PostMapping("/api/permissions/clear-cache")
public Result<String> clearCache() {
    permissionCacheService.clearAllCache();
    return Result.success("缓存已清除");
}
```

3. **前端重新获取权限**
```javascript
// 退出登录时清除本地缓存
async logout() {
  await store.dispatch('user/logout')
  location.reload()
}
```

### Q2: 为什么有按钮权限还要接口权限？

**A:** 这是多层防护的设计：

1. **前端按钮权限**：提升用户体验，隐藏无权限操作
2. **后端接口权限**：最终安全防线，防止绕过前端
3. **独立配置**：可以实现"试用账号"等特殊场景

类比：
- 前端按钮 = 家门的门锁（防止误闯）
- 后端接口 = 保险柜的锁（最终保护）

### Q3: 如何实现"按钮置灰"效果？

**A:** 按钮置灰是UI状态控制，不是权限控制：

```vue
<template>
  <el-button
    v-if="hasPermission('advertiser:create')"  <!-- 有权限才显示 -->
    :disabled="!canCreate"  <!-- 根据业务规则控制是否可用 -->
  >
    新增广告主
  </el-button>
</template>

<script setup>
const canCreate = computed(() => {
  // 业务规则判断
  return user.advertiserCount < user.maxAdvertisers
})
</script>
```

### Q4: 权限继承会导致安全问题吗？

**A:** 不会，因为：

1. **只有菜单权限向上继承**
   - 目的：解决"有子菜单看不到父菜单"的问题
   - 效果：能展开菜单树

2. **按钮和接口权限不继承**
   - 目的：精确控制，避免权限过度开放
   - 效果：必须精确匹配

3. **最终安全防线在后端**
   - 前端权限：用户体验
   - 后端权限：安全保障

### Q5: 如何调试权限问题？

**A:** 调试步骤：

1. **查看数据库中的权限配置**
```sql
-- 查看用户的角色
SELECT r.*
FROM sys_role r
JOIN sys_user_role ur ON r.id = ur.role_id
WHERE ur.user_id = 1;

-- 查看角色的权限
SELECT p.*
FROM sys_permission p
JOIN sys_role_permission rp ON p.id = rp.permission_id
WHERE rp.role_id IN (1, 2, 3);
```

2. **查看后端日志**
```
DEBUG - 从缓存获取用户权限: userId=1, permissionsCount=15
DEBUG - 权限校验通过: userId=1, apiPermissions=[api:advertiser:create]
WARN - API权限不足: userId=1, requiredApiPermissions=[api:advertiser:delete]
```

3. **查看前端存储的权限**
```javascript
// 浏览器控制台
console.log(store.state.user.permissions)

// 或使用Vue DevTools查看Vuex状态
```

4. **查看Redis缓存**
```bash
redis-cli
> KEYS "user:permissions:*"
> GET "user:permissions:1"
```

### Q6: 如何批量添加权限？

**A:** 使用SQL脚本批量添加：

```sql
-- 批量为所有Controller添加接口权限
-- 假设已经存在按钮权限

INSERT INTO sys_permission (parent_id, permission_code, permission_name, permission_type, path, method, sort_order, status)
SELECT
    parent_id,
    CONCAT('api:', permission_code) as permission_code,
    CONCAT(permission_name, '接口') as permission_name,
    3 as permission_type,
    path,
    method,
    sort_order + 10 as sort_order,
    status
FROM sys_permission
WHERE permission_type = 2
  AND permission_code LIKE '%:%'
  AND NOT EXISTS (
    SELECT 1 FROM sys_permission p2
    WHERE p2.permission_code = CONCAT('api:', sys_permission.permission_code)
  );
```

---

## 附录

### A.1 权限编码规范

#### 菜单权限编码
```
格式: {模块名}
示例:
- advertiser (广告主管理)
- system (系统管理)
- system:user (用户管理)
- system:role (角色管理)
```

#### 按钮权限编码
```
格式: {模块名}:{操作}
示例:
- advertiser:create (新增广告主)
- advertiser:update (编辑广告主)
- advertiser:delete (删除广告主)
- advertiser:query (查看广告主)
- advertiser:audit (审核广告主)
```

#### 接口权限编码
```
格式: api:{模块名}:{操作}
示例:
- api:advertiser:create (创建广告主接口)
- api:advertiser:list (广告主列表接口)
- api:advertiser:detail (广告主详情接口)
- api:advertiser:update (更新广告主接口)
- api:advertiser:delete (删除广告主接口)
```

### A.2 相关文件清单

#### 后端文件
- 注解定义：
  - [RequiresPermission.java](zhuque-backend/zhuque-common/src/main/java/wake/su/zhuque/common/security/annotation/RequiresPermission.java)
  - [RequiresApiPermission.java](zhuque-backend/zhuque-common/src/main/java/wake/su/zhuque/common/security/annotation/RequiresApiPermission.java)
- 切面实现：
  - [PermissionAspect.java](zhuque-backend/zhuque-common/src/main/java/wake/su/zhuque/common/security/aspect/PermissionAspect.java)
  - [ApiPermissionAspect.java](zhuque-backend/zhuque-common/src/main/java/wake/su/zhuque/common/security/aspect/ApiPermissionAspect.java)
- Controller示例：
  - [AdvertiserController.java](zhuque-backend/web-admin/src/main/java/wake/su/zhuque/controller/AdvertiserController.java)

#### 前端文件
- 权限工具：`src/utils/permission.js`
- 路由配置：`src/router/index.js`
- Vuex Store：`src/store/modules/user.js`

#### 数据库文件
- 初始化脚本：[schema.sql](zhuque-backend/web-admin/src/main/resources/db/schema.sql)

### A.3 参考资料

- Spring AOP文档：https://docs.spring.io/spring-framework/reference/core/aop.html
- MyBatis-Plus文档：https://baomidou.com/
- RBAC权限模型：https://en.wikipedia.org/wiki/Role-based_access_control

---

**文档版本**: v2.0
**最后更新**: 2026-01-11
**维护者**: wake.su
