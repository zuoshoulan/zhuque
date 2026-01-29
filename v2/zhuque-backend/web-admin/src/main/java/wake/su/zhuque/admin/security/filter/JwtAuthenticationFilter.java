package wake.su.zhuque.admin.security.filter;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import wake.su.zhuque.common.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT认证过滤器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    // 从请求头获取Authorization
    String authHeader = request.getHeader("Authorization");

    // 验证Token
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7); // 去掉 "Bearer " 前缀

      try {
        // 验证Token有效性
        if (jwtUtil.validateToken(token)) {
          // 获取用户ID
          Long userId = jwtUtil.getUserId(token);
          String username = jwtUtil.getUsername(token);

          // 将用户名放入请求头，供MetaObjectHandler使用
          request.setAttribute("X-User-Name", username);

          // 创建认证信息
          UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null,
              Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

          // 设置详细信息
          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

          // 设置到Security上下文
          SecurityContextHolder.getContext().setAuthentication(authentication);

          log.debug("JWT认证成功: userId={}, username={}", userId, username);
        } else {
          log.warn("JWT Token无效或已过期: {}", token.substring(0, Math.min(20, token.length())) + "...");
        }
      } catch(Exception e) {
        log.error("JWT认证失败: {}", e.getMessage());
      }
    }

    // 继续执行后续过滤器
    filterChain.doFilter(request, response);
  }
}
