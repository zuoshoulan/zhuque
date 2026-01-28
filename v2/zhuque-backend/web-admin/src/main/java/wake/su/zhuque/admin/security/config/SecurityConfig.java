package wake.su.zhuque.admin.security.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import wake.su.zhuque.admin.security.filter.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

/**
 * Spring Security 配置
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

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
  public SecurityFilterChain securityFilterChain(HttpSecurity http, PermitAllProperties permitAllProperties)
      throws Exception {
    http
        // 禁用 CSRF（使用 JWT 时不需要）
        .csrf(AbstractHttpConfigurer::disable)
        // 配置请求授权
        .authorizeHttpRequests(authorize -> authorize
            // 允许匿名访问的路径（从配置文件读取）
            .requestMatchers(permitAllProperties.getPaths().toArray(new String [ 0 ])).permitAll()
            // 其他所有请求都需要认证
            .anyRequest().authenticated())
        // 添加JWT过滤器
        .addFilterBefore(jwtAuthenticationFilter,
            org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
        // 禁用 form 登录
        .formLogin(AbstractHttpConfigurer::disable)
        // 禁用 HTTP Basic
        .httpBasic(AbstractHttpConfigurer::disable);

    return http.build();
  }
}
