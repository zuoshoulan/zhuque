package wake.su.zhuque.common.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

/**
 * Spring Security 配置
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Security 配置属性
     */
    @ConfigurationProperties(prefix = "security.permit-all")
    @Configuration
    public static class PermitAllProperties {
        private List<String> paths;

        public List<String> getPaths() {
            return paths;
        }

        public void setPaths(List<String> paths) {
            this.paths = paths;
        }
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, PermitAllProperties permitAllProperties) throws Exception {
        http
            // 禁用 CSRF（使用 JWT 时不需要）
            .csrf(AbstractHttpConfigurer::disable)
            // 配置请求授权
            .authorizeHttpRequests(authorize -> authorize
                // 允许匿名访问的路径（从配置文件读取）
                .requestMatchers(permitAllProperties.getPaths().toArray(new String[0])).permitAll()
                // 其他所有请求都需要认证
                .anyRequest().authenticated()
            )
            // 禁用 form 登录
            .formLogin(AbstractHttpConfigurer::disable)
            // 禁用 HTTP Basic
            .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
