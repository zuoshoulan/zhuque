# 初始用户和权限初始化指南

> **关键问题**: 第一个用户是谁？如何创建第二个用户？
> **文档版本**: v1.0
> **编写日期**: 2025-01-07

---

## 一、初始用户（第一个用户）

### 1.1 初始用户信息

**用户名**: `admin`

**密码**: `20250107a`（创建日期 + 用户名首字母）

**密码规则**: `YYYYMMDD` + 用户名首字母
- 例如：admin创建于2025年01月07日
- 密码 = `20250107` + `a` = `20250107a`

**角色**: 超级管理员

**权限**: `*:*:*`（拥有所有权限）

**首次登录**: ✅ 必须修改密码

### 1.2 初始用户创建方式

#### 方式1：数据库初始化脚本（推荐）✅

**文件位置**: `docker/mysql/init/init.sql`

```sql
-- 插入超级管理员用户（密码：admin123，BCrypt加密）
INSERT INTO sys_user (username, password, real_name, status, user_type) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 1, 1)
ON DUPLICATE KEY UPDATE username=username;

-- 插入超级管理员角色
INSERT INTO sys_role (role_name, role_code, description, status) VALUES
('超级管理员', 'admin', '拥有所有权限', 1)
ON DUPLICATE KEY UPDATE role_name=role_name;

-- 关联用户和角色
INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id
FROM sys_user u, sys_role r
WHERE u.username = 'admin' AND r.role_code = 'admin'
ON DUPLICATE KEY UPDATE user_id=user_id;

-- 为超级管理员角色分配所有权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r, sys_permission p
WHERE r.role_code = 'admin'
ON DUPLICATE KEY UPDATE role_id=role_id;
```

**优势**：
- ✅ 自动创建，无需手动操作
- ✅ Docker启动时自动执行
- ✅ 密码已加密（BCrypt）
- ✅ 默认拥有所有权限
- ✅ 强制首次登录修改密码（`force_change_password=1`）

#### 方式2：手动插入（备用）

```sql
-- 1. 创建用户（密码需要BCrypt加密，并设置强制修改密码）
-- 密码：20250107a（需要先通过BCrypt加密）
INSERT INTO sys_user (username, password, real_name, force_change_password)
VALUES ('admin', '$2a$10$...', '系统管理员', 1);

-- 2. 创建角色
INSERT INTO sys_role (role_name, role_code)
VALUES ('超级管理员', 'admin');

-- 3. 关联用户和角色
INSERT INTO sys_user_role (user_id, role_id)
VALUES (1, 1);

-- 4. 分配权限（如果已有权限数据）
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission;
```

**密码生成**：
```java
// 生成初始密码
String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
String initialPassword = date + "a";  // 20250107a

// BCrypt加密
String encodedPassword = passwordEncoder.encode(initialPassword);
```

---

## 二、强制修改密码机制

### 2.1 数据库字段

用户表新增字段：

```sql
-- 强制修改密码标志
force_change_password TINYINT DEFAULT 0 COMMENT '是否强制修改密码：1-是 0-否'

-- 上次修改密码时间
last_change_password_time DATETIME COMMENT '上次修改密码时间'
```

### 2.2 后端实现

#### 登录接口检查

```java
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public LoginVO login(String username, String password) {
        // 1. 查询用户
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 2. 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 3. 检查是否需要强制修改密码
        if (user.getForceChangePassword() == 1) {
            // 生成临时Token（仅用于修改密码）
            String tempToken = jwtTokenProvider.generateTempToken(user.getId());

            LoginVO vo = new LoginVO();
            vo.setToken(tempToken);
            vo.setForceChangePassword(true);
            vo.setMessage("首次登录，请修改密码");

            return vo;
        }

        // 4. 正常登录流程
        // ... 生成Token、查询权限等
    }
}
```

#### 修改密码接口

```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public Result<Void> changePassword(@RequestBody ChangePasswordDTO dto) {
        // 1. 验证旧密码
        // 2. 验证新密码强度
        // 3. 更新密码
        // 4. 清除force_change_password标志
        // 5. 更新last_change_password_time
        authService.changePassword(dto);
        return Result.success();
    }

    /**
     * 强制修改密码（首次登录）
     */
    @PostMapping("/force-change-password")
    public Result<Void> forceChangePassword(@RequestBody ForceChangePasswordDTO dto) {
        // 仅用于force_change_password=1的用户
        authService.forceChangePassword(dto);
        return Result.success();
    }
}
```

#### Service实现

```java
@Service
public class UserServiceImpl implements UserService {

    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        // 1. 查询用户
        User user = userMapper.selectById(userId);

        // 2. 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("旧密码错误");
        }

        // 3. 验证新密码强度
        validatePasswordStrength(newPassword);

        // 4. 加密新密码
        String encodedPassword = passwordEncoder.encode(newPassword);

        // 5. 更新密码
        userMapper.updatePassword(userId, encodedPassword);

        // 6. 更新修改时间
        userMapper.updateLastChangePasswordTime(userId, new Date());

        // 7. 清除强制修改标志（如果有）
        userMapper.clearForceChangePassword(userId);
    }

    @Override
    @Transactional
    public void forceChangePassword(Long userId, String newPassword) {
        // 1. 验证新密码强度
        validatePasswordStrength(newPassword);

        // 2. 加密新密码
        String encodedPassword = passwordEncoder.encode(newPassword);

        // 3. 更新密码
        userMapper.updatePassword(userId, encodedPassword);

        // 4. 更新修改时间
        userMapper.updateLastChangePasswordTime(userId, new Date());

        // 5. 清除强制修改标志
        userMapper.clearForceChangePassword(userId);
    }

    /**
     * 验证密码强度
     */
    private void validatePasswordStrength(String password) {
        if (password.length() < 8) {
            throw new BusinessException("密码长度至少8位");
        }

        // 至少包含大小写字母、数字
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");

        if (!hasUpper || !hasLower || !hasDigit) {
            throw new BusinessException("密码必须包含大小写字母和数字");
        }
    }
}
```

### 2.3 前端实现

#### 登录后检查

```typescript
// 登录响应
interface LoginResponse {
  token: string;
  forceChangePassword: boolean;
  message: string;
}

// 登录方法
const handleLogin = async () => {
  const response = await login(loginForm.value);

  if (response.forceChangePassword) {
    // 需要强制修改密码
    ElMessageBox.confirm('首次登录，请修改密码', '提示', {
      confirmButtonText: '去修改',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      // 跳转到修改密码页面
      router.push('/change-password');
    });
  } else {
    // 正常登录
    localStorage.setItem('token', response.token);
    router.push('/dashboard');
  }
};
```

#### 修改密码页面

```vue
<template>
  <div class="change-password-container">
    <el-card header="修改密码">
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-alert
          v-if="isForceChange"
          title="首次登录必须修改密码"
          type="warning"
          :closable="false"
          style="margin-bottom: 20px"
        />

        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="form.newPassword"
            type="password"
            placeholder="请输入新密码（至少8位，包含大小写字母和数字）"
            show-password
          />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
          />
        </el-form-item>

        <el-button type="primary" @click="handleChangePassword" :loading="loading">
          提交
        </el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { forceChangePassword } from '@/api/auth';
import { ElMessage } from 'element-plus';

const router = useRouter();
const formRef = ref();
const loading = ref(false);

const form = reactive({
  newPassword: '',
  confirmPassword: ''
});

const rules = {
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 8, message: '密码长度至少8位', trigger: 'blur' },
    {
      pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/,
      message: '密码必须包含大小写字母和数字',
      trigger: 'blur'
    }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== form.newPassword) {
          callback(new Error('两次输入的密码不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ]
};

const handleChangePassword = async () => {
  await formRef.value.validate();

  loading.value = true;
  try {
    await forceChangePassword({
      newPassword: form.newPassword
    });

    ElMessage.success('密码修改成功，请重新登录');
    router.push('/login');
  } finally {
    loading.value = false;
  }
};
</script>
```

---

## 三、初始用户权限

### 2.1 超级管理员权限

**权限标识**: `*:*:*`

**权限范围**:
- ✅ 所有页面（路由权限）
- ✅ 所有按钮（按钮权限）
- ✅ 所有API接口（接口权限）

**特殊处理**:
```java
// 权限拦截器中判断
if (permissions.contains("*:*:*")) {
    // 超级管理员，直接放行
    return true;
}
```

### 2.2 初始权限数据

**初始化脚本包含的基础权限**：

```sql
-- 系统管理
INSERT INTO sys_permission (permission_name, permission_code, permission_type) VALUES
('系统管理', 'system', 1),
('用户管理', 'system:user', 1),
('用户查询', 'system:user:list', 3),
('用户新增', 'system:user:create', 2),
('用户编辑', 'system:user:update', 2),
('用户删除', 'system:user:delete', 2),
('角色管理', 'system:role', 1),
('权限管理', 'system:permission', 1);
```

---

## 三、创建第二个用户的完整流程

### 3.1 操作流程图

```
步骤1：使用admin登录
  ↓
步骤2：进入"用户管理"页面
  ↓
步骤3：点击"新增用户"按钮
  ↓
步骤4：填写用户信息
  ↓
步骤5：为用户分配角色
  ↓
步骤6：保存用户
  ↓
步骤7：新用户可以登录
```

### 3.2 详细步骤

#### 步骤1：登录系统

```
URL: http://localhost:5173/login
用户名: admin
密码: admin123
```

**后端验证**：
```java
// 1. 验证用户名密码
User user = userService.findByUsername("admin");
if (passwordEncoder.matches("admin123", user.getPassword())) {
    // 密码正确
}

// 2. 生成Token
String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername());

// 3. 查询用户权限
Set<String> permissions = userService.getUserPermissions(user.getId());
// permissions = ["*:*:*"]

// 4. 缓存到Redis
redisTemplate.opsForValue().set("auth:user:permissions:1", permissions);

// 5. 返回Token和用户信息
```

#### 步骤2：创建第二个用户

**前端操作**：
```
1. 点击侧边栏"系统管理" → "用户管理"
2. 点击右上角"+ 新增用户"按钮
3. 弹出"新增用户"对话框
```

**填写信息**：
```
用户名: testuser
真实姓名: 测试用户
密码: 123456
邮箱: test@example.com
手机号: 13800138000
状态: 启用
```

**分配角色**：
```
☑ 运营人员
☐ 渠道管理员
```

**点击"确定"按钮**

#### 步骤3：后端处理

**Controller**:
```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @RequiresPermission("system:user:create")  // 权限验证
    @PostMapping
    public Result<Long> create(@RequestBody UserCreateDTO dto) {
        // 1. 创建用户
        Long userId = userService.createUser(dto);

        // 2. 分配角色
        if (CollectionUtils.isNotEmpty(dto.getRoleIds())) {
            userService.assignRoles(userId, dto.getRoleIds());
        }

        // 3. 清除缓存（如果需要）
        // userPermissionCache.clearUserCache(userId);

        return Result.success(userId);
    }
}
```

**Service**:
```java
@Service
public class UserServiceImpl implements UserService {

    @Override
    @Transactional
    public Long createUser(UserCreateDTO dto) {
        // 1. 检查用户名是否已存在
        if (userMapper.findByUsername(dto.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }

        // 2. 密码加密
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        // 3. 创建用户
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(encodedPassword);
        user.setRealName(dto.getRealName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setStatus(1);

        userMapper.insert(user);

        // 4. 分配角色
        if (CollectionUtils.isNotEmpty(dto.getRoleIds())) {
            assignRoles(user.getId(), dto.getRoleIds());
        }

        return user.getId();
    }

    @Override
    public void assignRoles(Long userId, List<Long> roleIds) {
        // 1. 删除原有角色
        userRoleMapper.deleteByUserId(userId);

        // 2. 插入新角色
        if (CollectionUtils.isNotEmpty(roleIds)) {
            userRoleMapper.batchInsert(userId, roleIds);
        }

        // 3. 清除用户权限缓存
        userPermissionCache.clearUserCache(userId);
    }
}
```

**数据库操作**：
```sql
-- 1. 插入用户
INSERT INTO sys_user (username, password, real_name, email, phone, status)
VALUES ('testuser', '$2a$10$...', '测试用户', 'test@example.com', '13800138000', 1);

-- 2. 关联角色
INSERT INTO sys_user_role (user_id, role_id) VALUES
(2, 2);  -- 假设角色ID=2是"运营人员"
```

#### 步骤4：新用户登录

**新用户信息**：
```
用户名: testuser
密码: 123456
```

**权限**：
- 继承角色的权限
- 如果角色是"运营人员"，则拥有运营人员角色的所有权限

---

## 四、创建新角色的流程

### 4.1 为什么需要创建角色？

初始数据中通常只有：
- 超级管理员（admin）
- 可能还有1-2个基础角色（运营人员、渠道管理员）

实际业务中需要：
- 销售经理角色
- 财务角色
- 审核员角色
- ...

### 4.2 创建角色流程

#### 步骤1：进入角色管理页面

```
侧边栏: 系统管理 → 角色管理
```

#### 步骤2：点击"新增角色"

**填写信息**：
```
角色名称: 销售经理
角色编码: sales_manager
描述: 负责广告主销售和管理工作
```

**点击"确定"**

#### 步骤3：为角色分配权限

**操作**：
```
1. 在角色列表中找到"销售经理"角色
2. 点击"分配权限"按钮
3. 弹出权限树对话框
```

**勾选权限**：
```
□ 全选

☑ 广告管理
  ☑ 广告主管理
    ☑ 广告主列表
    ☑ 新增广告主
    ☑ 编辑广告主
  ☑ 推广活动管理
    ☑ 活动列表
    ☑ 新增活动
    ☑ 编辑活动
```

**点击"确定"**

#### 步骤4：为用户分配角色

现在可以创建用户并分配"销售经理"角色：
```
1. 新增用户
2. 分配角色时勾选"销售经理"
3. 保存
```

---

## 五、权限初始化检查清单

### 5.1 数据库检查

启动项目后，执行以下SQL验证：

```sql
-- 1. 检查初始用户
SELECT * FROM sys_user WHERE username = 'admin';
-- 应该返回1条记录

-- 2. 检查初始角色
SELECT * FROM sys_role WHERE role_code = 'admin';
-- 应该返回1条记录

-- 3. 检查用户角色关联
SELECT * FROM sys_user_role WHERE user_id = 1;
-- 应该返回1条记录

-- 4. 检查权限数据
SELECT COUNT(*) FROM sys_permission;
-- 应该返回基础权限数量

-- 5. 检查角色权限关联
SELECT COUNT(*) FROM sys_role_permission WHERE role_id = 1;
-- 应该返回权限总数（超级管理员拥有所有权限）
```

### 5.2 功能检查

**登录测试**：
```bash
# 使用Postman或curl测试
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**预期返回**：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "id": 1,
      "username": "admin",
      "realName": "系统管理员",
      "roles": ["admin"],
      "permissions": ["*:*:*"]
    }
  }
}
```

**权限测试**：
```bash
# 使用Token访问接口
GET http://localhost:8080/api/users
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**预期返回**：
```json
{
  "code": 200,
  "data": [...]
}
```

---

## 六、常见问题

### Q1: 忘记admin密码怎么办？

**方案1：数据库重置**
```sql
-- 生成新的BCrypt密码（123456）
-- $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi

UPDATE sys_user
SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi'
WHERE username = 'admin';
```

**方案2：提供重置密码接口（推荐）**
```java
@PostMapping("/api/users/{id}/reset-password")
@RequiresPermission("system:user:reset-password")
public Result<Void> resetPassword(@PathVariable Long id) {
    userService.resetPassword(id, "123456");
    return Result.success();
}
```

### Q2: 如何修改初始密码？

**第一次登录强制修改密码**：
```java
// 用户表增加字段
ALTER TABLE sys_user ADD COLUMN force_change_password TINYINT DEFAULT 0;

// 登录时检查
if (user.getForceChangePassword() == 1) {
    return Result.error("请修改初始密码");
}
```

### Q3: 如何禁用admin账户？

```sql
-- 方式1：禁用用户
UPDATE sys_user SET status = 0 WHERE username = 'admin';

-- 方式2：删除用户（不推荐）
DELETE FROM sys_user WHERE username = 'admin';
```

**注意**：禁用或删除admin前，确保有其他超级管理员账户。

### Q4: 数据库被清空了怎么办？

**重新执行初始化脚本**：
```bash
mysql -u root -p zhuque_dev < docker/mysql/init/init.sql
```

---

## 七、安全建议

### 7.1 生产环境必做

1. **修改初始密码**
   ```
   首次登录后立即修改admin密码
   密码要求：至少12位，包含大小写字母、数字、特殊字符
   ```

2. **启用双因素认证（2FA）**（可选）
   ```
   Google Authenticator
   短信验证码
   邮箱验证码
   ```

3. **限制登录IP**
   ```sql
   CREATE TABLE sys_user_whitelist (
     user_id BIGINT NOT NULL,
     ip VARCHAR(50) NOT NULL,
     PRIMARY KEY (user_id, ip)
   );
   ```

4. **记录登录日志**
   ```java
   // 已在数据库设计中实现
   CREATE TABLE sys_login_log (
     user_id BIGINT,
     login_ip VARCHAR(50),
     login_time DATETIME,
     status TINYINT
   );
   ```

### 7.2 开发环境

- 可以使用简单密码（admin123）
- 无需限制IP
- 可以保留默认admin账户

---

## 八、完整操作示例

### 8.1 场景：创建一个运营人员

**目标**：创建一个运营人员账户，可以管理广告主和推广活动

**步骤**：

1. **登录admin账户**
   ```
   URL: http://localhost:5173/login
   用户名: admin
   密码: admin123
   ```

2. **创建运营人员角色**（如果不存在）
   ```
   系统管理 → 角色管理 → 新增角色

   角色名称: 运营人员
   角色编码: operator
   描述: 负责日常运营工作
   ```

3. **为角色分配权限**
   ```
   角色管理 → 找到"运营人员" → 分配权限

   勾选权限：
   ☑ 广告管理
     ☑ 广告主管理（查看、新增、编辑）
     ☑ 推广活动管理（查看、新增、编辑）
     ☑ 创意管理（查看、新增）

   点击"确定"
   ```

4. **创建运营人员账户**
   ```
   用户管理 → 新增用户

   用户名: operator1
   真实姓名: 张三
   密码: 123456
   邮箱: zhangsan@example.com
   分配角色: ☑ 运营人员

   点击"确定"
   ```

5. **验证新账户**
   ```
   退出登录
   使用 operator1 / 123456 登录
   验证只能看到广告管理相关菜单
   ```

---

## 九、总结

### 初始化流程总结

```
1. Docker启动MySQL
   ↓
2. 自动执行init.sql
   ↓
3. 创建admin用户（超级管理员）
   ↓
4. 创建基础角色和权限
   ↓
5. admin登录系统
   ↓
6. 创建其他角色（销售经理、财务等）
   ↓
7. 为角色分配权限
   ↓
8. 创建普通用户并分配角色
   ↓
9. 普通用户登录并工作
```

### 关键要点

- ✅ 第一个用户是admin（超级管理员）
- ✅ admin由数据库初始化脚本自动创建
- ✅ admin拥有所有权限（*:*:*）
- ✅ 必须使用admin创建其他用户和角色
- ✅ 生产环境务必修改admin初始密码

---

**文档版本**: v1.0
**最后更新**: 2025-01-07
**维护者**: 开发团队
