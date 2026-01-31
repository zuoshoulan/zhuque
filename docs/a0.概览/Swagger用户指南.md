# Swagger/OpenAPI 使用指南

## 🚀 访问地址

应用启动后，可以通过以下地址访问Swagger文档：

### 开发环境

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs
- **OpenAPI YAML**: http://localhost:8080/v3/api-docs.yaml

---

## 🔐 JWT认证配置

### 步骤1：登录获取Token

在Swagger UI中测试登录接口：

1. 展开 `认证授权` 分组
2. 点击 `POST /api/auth/login` 接口
3. 点击 "Try it out"
4. 输入请求体：
```json
{
  "account": "admin",
  "password": "Admin123"
}
```
5. 点击 "Execute" 执行
6. 从响应中复制 `accessToken`（不包含 `Bearer ` 前缀）

### 步骤2：配置全局认证

1. 点击页面右上角的 **🔓 Authorize** 按钮
2. 在弹出框中输入Token（格式：`Bearer your-access-token-here`）
   - **注意**：必须包含 `Bearer ` 前缀，注意Bearer后面有一个空格
3. 点击 **Authorize** 按钮
4. 看到绿色提示 "Authorized" 表示成功
5. 关闭弹窗

### 步骤3：测试需要认证的接口

现在所有请求都会自动携带JWT Token，可以正常调用需要认证的接口。

---

## 📚 API分组说明

### 已实现的分组

#### 1. 认证授权
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户登出

#### 2. 权限管理
- `POST /api/permissions` - 创建权限
- `PUT /api/permissions/{id}` - 更新权限
- `DELETE /api/permissions/{id}` - 删除权限
- `GET /api/permissions/{id}` - 获取权限详情
- `GET /api/permissions/tree` - 获取权限树
- `GET /api/permissions/list` - 获取权限列表

#### 3. 角色管理
- `POST /api/roles` - 创建角色
- `PUT /api/roles/{id}` - 更新角色
- `DELETE /api/roles/{id}` - 删除角色
- `GET /api/roles/{id}` - 获取角色详情
- `GET /api/roles/list` - 获取角色列表
- `POST /api/roles/{id}/permissions` - 分配权限
- `GET /api/roles/{id}/permission-ids` - 获取角色权限ID列表
- `GET /api/roles/{id}/permissions` - 获取角色权限列表

#### 4. 菜单管理
- `GET /api/menus/user/tree` - 获取当前用户菜单树
- `GET /api/menus/tree` - 获取所有菜单树
- `GET /api/menus/list` - 获取所有菜单列表

#### 5. 广告主管理（示例）
- `POST /api/advertisers` - 创建广告主
- `PUT /api/advertisers/{id}` - 更新广告主
- `DELETE /api/advertisers/{id}` - 删除广告主
- `GET /api/advertisers/{id}` - 获取广告主详情
- `GET /api/advertisers` - 获取广告主列表

---

## 💡 使用技巧

### 1. 快速搜索接口

- 使用Swagger UI页面顶部的搜索框
- 输入关键词（如 "permission"、"role"）快速过滤接口

### 2. 查看Schema模型

- 页面底部有 "Schemas" 分组
- 展开可查看所有DTO、VO的数据结构
- 包含字段说明、类型、示例值

### 3. 下载API文档

1. 点击页面顶部的 **API文档** 链接
2. 可以选择下载 JSON 或 YAML 格式
3. 支持导入到 Postman、Apifox 等工具

### 4. 测试接口

1. 点击接口展开详情
2. 点击 **Try it out** 按钮
3. 填写参数（必填项会标红）
4. 点击 **Execute** 执行
5. 查看响应结果

### 5. 查看请求历史

- Swagger UI会记录最近的请求
- 方便调试和回溯

---

## 🎯 注解说明

### Controller注解

```java
@Tag(name = "权限管理", description = "权限CRUD、树形结构查询接口")
@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    @Operation(
        summary = "创建权限",
        description = "创建新的权限，支持路由、按钮、接口三种类型"
    )
    @ApiResponse(responseCode = "200", description = "创建成功")
    @PostMapping
    public Result<Long> createPermission(@RequestBody PermissionCreateRequest request) {
        // ...
    }
}
```

### DTO注解

```java
@Schema(description = "权限创建请求")
public class PermissionCreateRequest {

    @Schema(description = "权限编码", example = "advertiser:create")
    @NotBlank(message = "权限编码不能为空")
    private String permissionCode;

    @Schema(description = "权限类型：1-路由 2-按钮 3-接口", example = "2")
    @NotNull(message = "权限类型不能为空")
    private Integer permissionType;
}
```

### 常用注解列表

| 注解 | 用途 | 示例 |
|-----|------|------|
| `@Tag` | Controller分组 | `@Tag(name = "用户管理")` |
| `@Operation` | 接口描述 | `@Operation(summary = "创建用户")` |
| `@Parameter` | 参数说明 | `@Parameter(description = "用户ID")` |
| `@ApiResponse` | 响应说明 | `@ApiResponse(responseCode = "200")` |
| `@Schema` | 模型说明 | `@Schema(description = "用户名")` |

---

## ⚠️ 注意事项

### 1. 生产环境禁用

生产环境建议禁用Swagger，避免泄露接口信息：

```yaml
# application-prod.yml
springdoc:
  swagger-ui:
    enabled: false
  api-docs:
    enabled: false
```

或通过Spring Security限制访问：

```java
http.authorizeHttpRequests(auth -> auth
    .requestMatchers("/swagger-ui/**").hasRole("ADMIN")
    .requestMatchers("/v3/api-docs/**").hasRole("ADMIN")
);
```

### 2. Token过期

- JWT Token默认7天有效期
- 如果返回 `401 Unauthorized`，说明Token已过期
- 需要重新登录获取新Token

### 3. 跨域问题

如果遇到CORS错误，确保：
- 后端已配置CORS（已配置）
- 前端请求头正确

---

## 📖 参考文档

- [SpringDoc官方文档](https://springdoc.org/)
- [OpenAPI 3规范](https://swagger.io/specification/)
- [Swagger注解文档](https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Annotations)

---

## 🐛 常见问题

### Q1: 访问 /swagger-ui.html 显示404

**A:** 确认：
1. 应用已正常启动
2. 端口正确（默认8080）
3. 检查是否被Spring Security拦截

### Q2: 接口显示 "401 Unauthorized"

**A:**
1. 先调用 `/api/auth/login` 获取Token
2. 点击右上角 "Authorize" 配置Token
3. Token格式：`Bearer your-token-here`

### Q3: Schema显示不完整

**A:** 确保DTO类添加了 `@Schema` 注解

### Q4: 接口分组不显示

**A:** 确保Controller类添加了 `@Tag` 注解

---

**文档版本：** v1.0
**编写日期：** 2026-01-09
**作者：** wake.su
