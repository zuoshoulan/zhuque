# Swagger/OpenAPI 集成计划

## 📋 集成目标

为朱雀项目后端添加API文档功能，使用 `springdoc-openapi` (OpenAPI 3) 替代旧版Swagger 2。

## 🛠️ 技术选型

### SpringDoc OpenAPI (OpenAPI 3)

**选择理由：**
- ✅ 原生支持Spring Boot 3.x
- ✅ 基于OpenAPI 3规范（现代标准）
- ✅ 自动生成API文档
- ✅ 支持JWT认证集成
- ✅ 提供Swagger UI界面
- ✅ 活跃维护，社区支持好

**替代方案对比：**
- ❌ Springfox Swagger 2：不支持Spring Boot 3.x，已停止维护
- ✅ **SpringDoc OpenAPI**：官方推荐，支持最新Spring Boot

## 📦 依赖配置

### Maven依赖

在 `zhuque-common/pom.xml` 中添加：

```xml
<!-- SpringDoc OpenAPI (Swagger UI) -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

**说明：**
- 版本：2.3.0（与Spring Boot 3.2.x兼容）
- 包含Swagger UI和OpenAPI 3规范

## 🔧 配置方案

### 1. OpenAPI基本配置

创建配置类：`zhuque-common/src/main/java/wake/su/zhuque/common/web/config/OpenApiConfig.java`

```java
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("朱雀广告平台 API")
                .version("2.0.0")
                .description("一站式广告平台后端服务接口文档")
                .contact(new Contact()
                    .name("开发团队")
                    .email("admin@zhuque.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
            .externalDocs(new ExternalDocumentation()
                .description("项目文档")
                .url("https://github.com/zuoshoulan/zhuque"));
    }
}
```

### 2. JWT认证配置

在OpenAPI配置中添加JWT安全方案：

```java
@Bean
public OpenAPI customOpenAPI() {
    return new OpenAPI()
        // ... info配置
        .components(new Components()
            .addSecuritySchemes("bearer-jwt",
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")))
        .addSecurityItem(new SecurityRequirement()
            .addList("bearer-jwt"));
}
```

### 3. application.yml配置

```yaml
# SpringDoc配置
springdoc:
  api-docs:
    enabled: true
    path: /v3/api-docs
  swagger-ui:
    enabled: true
    path: /swagger-ui.html
    tags-sorter: alpha
    operations-sorter: alpha
  show-actuator: false
  group-configs:
    - group: 'public'
      paths-to-match: '/api/**'
      packages-to-scan: 'wake.su.zhuque.controller'
```

## 📚 使用示例

### 1. Controller注解示例

```java
@RestController
@RequestMapping("/api/permissions")
@Tag(name = "权限管理", description = "权限CRUD接口")
public class PermissionController {

    @Operation(summary = "创建权限", description = "创建新的权限")
    @Parameter(name = "request", description = "权限创建请求", required = true)
    @ApiResponse(responseCode = "200", description = "创建成功")
    @ApiResponse(responseCode = "400", description = "参数错误")
    @PostMapping
    public Result<Long> createPermission(
            @Valid @RequestBody PermissionCreateRequest request) {
        // ...
    }
}
```

### 2. 常用注解

| 注解 | 用途 | 示例 |
|-----|------|------|
| `@Tag` | Controller分组 | `@Tag(name = "权限管理")` |
| `@Operation` | 接口描述 | `@Operation(summary = "创建权限")` |
| `@Parameter` | 参数说明 | `@Parameter(name = "id", description = "权限ID")` |
| `@ApiResponse` | 响应说明 | `@ApiResponse(responseCode = "200")` |
| `@Schema` | 模型说明 | `@Schema(description = "用户名")` |

### 3. DTO模型注解

```java
@Schema(description = "权限创建请求")
public class PermissionCreateRequest {

    @Schema(description = "权限编码", example = "advertiser:create", required = true)
    @NotBlank(message = "权限编码不能为空")
    private String permissionCode;

    @Schema(description = "权限类型：1-路由 2-按钮 3-接口", example = "2", required = true)
    @NotNull(message = "权限类型不能为空")
    private Integer permissionType;
}
```

## 🎯 API分组方案

### 按模块分组

```java
// 认证模块
@Tag(name = "认证授权", description = "用户登录、登出、Token刷新")
public class AuthController { }

// 权限管理模块
@Tag(name = "权限管理", description = "权限CRUD、树形结构")
public class PermissionController { }

// 角色管理模块
@Tag(name = "角色管理", description = "角色CRUD、权限分配")
public class RoleController { }

// 广告主管理模块
@Tag(name = "广告主管理", description = "广告主CRUD、审核")
public class AdvertiserController { }
```

### 分组展示效果

Swagger UI页面会自动按Tag分组展示API。

## 🔐 JWT认证集成

### 在Swagger UI中测试需要认证的接口

1. **配置JWT安全方案**（见上方配置）
2. **在Swagger UI中设置Token**
   - 点击页面右上角 "Authorize" 按钮
   - 输入JWT Token（格式：`Bearer your-token-here`）
   - 点击 "Authorize" 确认
3. **后续请求会自动携带Token**

### 示例代码

```java
@Operation(summary = "获取权限详情", security = @SecurityRequirement(name = "bearer-jwt"))
@RequirePermission("permission:query")
@GetMapping("/{id}")
public Result<PermissionVO> getPermission(
        @Parameter(description = "权限ID", required = true)
        @PathVariable Long id) {
    // ...
}
```

## 🌐 访问地址

### 开发环境

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`
- **OpenAPI YAML**: `http://localhost:8080/v3/api-docs.yaml`

### 生产环境

**建议：**
- 仅在开发和测试环境启用
- 生产环境禁用（`springdoc.swagger-ui.enabled=false`）
- 或通过Spring Security限制访问权限

## ✅ 实施步骤

1. ✅ **添加依赖** - 在 `zhuque-common/pom.xml` 添加 `springdoc-openapi-starter-webmvc-ui`
2. ✅ **创建配置类** - `OpenApiConfig.java` 配置基本信息和JWT
3. ✅ **添加配置文件** - `application.yml` 配置SpringDoc
4. ✅ **注解Controller** - 为现有Controller添加 `@Tag`、`@Operation` 等注解
5. ✅ **注解DTO** - 为DTO添加 `@Schema` 注解
6. ✅ **测试访问** - 启动应用，访问 Swagger UI 测试
7. ✅ **JWT集成测试** - 在Swagger UI中测试需要认证的接口

## 📝 后续优化

- [ ] 添加全局异常处理说明
- [ ] 自定义Swagger UI主题
- [ ] 导出离线API文档（PDF/HTML）
- [ ] API变更自动通知
- [ ] 集成接口测试（自动生成测试用例）

## 🔗 参考文档

- [SpringDoc官方文档](https://springdoc.org/)
- [OpenAPI 3规范](https://swagger.io/specification/)
- [Swagger注解文档](https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Annotations)

---

**文档版本：** v1.0
**编写日期：** 2026-01-09
**作者：** wake.su
