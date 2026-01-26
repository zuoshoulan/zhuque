package wake.su.zhuque.admin.web.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI (Swagger) 配置
 *
 * @author wake.su
 * @since 2026-01-09
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearer-jwt";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // API基本信息
                .info(new Info()
                        .title("朱雀广告平台 API")
                        .version("2.0.0")
                        .description("""
                                ## 朱雀广告平台后端服务接口文档

                                ### 功能模块
                                - **认证授权**：用户登录、登出、Token刷新
                                - **权限管理**：权限CRUD、树形结构查询
                                - **角色管理**：角色CRUD、权限分配
                                - **菜单管理**：菜单树查询、用户菜单
                                - **广告主管理**：广告主CRUD、审核流程

                                ### 认证说明
                                本系统使用JWT认证，访问需要认证的接口时：
                                1. 先调用 `/api/auth/login` 登录获取Token
                                2. 点击右上角 **Authorize** 按钮
                                3. 输入Token（格式：`Bearer your-token-here`）
                                4. 点击 **Authorize** 确认
                                5. 后续请求会自动携带Token

                                ### 响应格式
                                统一响应格式：
                                ```json
                                {
                                  "code": 200,
                                  "message": "success",
                                  "data": {},
                                  "timestamp": 1704451200000
                                }
                                ```
                                """)
                        .contact(new Contact()
                                .name("开发团队")
                                .email("admin@zhuque.com")
                                .url("https://github.com/zuoshoulan/zhuque"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html"))
                )
                // 外部文档
                .externalDocs(new ExternalDocumentation()
                        .description("项目文档")
                        .url("https://github.com/zuoshoulan/zhuque/wiki")
                )
                // JWT安全配置
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("请输入JWT Token，格式：Bearer {token}")
                        )
                )
                // 全局安全要求（所有接口默认需要认证）
                .addSecurityItem(new SecurityRequirement()
                        .addList(SECURITY_SCHEME_NAME)
                );
    }
}
