package wake.su.zhuque.bid.config;

import java.util.concurrent.Executors;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Tomcat 虚拟线程配置
 *
 * <p>配置 Tomcat 使用虚拟线程处理 HTTP 请求
 *
 * <p>收益：
 * <ul>
 * <li>大幅提升并发能力（从 200 个平台线程 → 数万虚拟线程）</li>
 * <li>降低内存占用（虚拟线程栈 ~几 KB vs 平台线程 ~1MB）</li>
 * <li>提升高 QPS 场景性能</li>
 * </ul>
 *
 * @author zhuque
 * @since 2025-01-29
 */
@Configuration
public class TomcatVirtualThreadConfig {

  /**
   * 配置 Tomcat 使用虚拟线程执行器
   */
  @Bean
  public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
    return factory -> {
      factory.addConnectorCustomizers(connector -> {
        // 使用虚拟线程执行器替换 Tomcat 默认的平台线程池
        connector.getProtocolHandler().setExecutor(
            Executors.newVirtualThreadPerTaskExecutor());
      });
    };
  }
}
