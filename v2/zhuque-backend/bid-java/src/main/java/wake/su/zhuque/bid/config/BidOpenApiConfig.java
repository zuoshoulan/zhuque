package wake.su.zhuque.bid.config;

import java.util.List;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

/**
 * OpenAPI / Swagger 配置 (bid-java)
 *
 * @author zhuque
 */
@Configuration
public class BidOpenApiConfig {

  @Bean
  public OpenAPI bidOpenAPI() {
    return new OpenAPI()
        .info(new Info().title("朱雀竞价服务 API").description("OpenRTB 2.6 实时竞价接口").version("1.0.0")
            .contact(new Contact().name("zhuque").email("tech@zhuque.com")).license(new License().name("MIT")))
        .servers(List.of(new Server().url("http://localhost:8081").description("本地开发环境"),
            new Server().url("https://api.zhuque.com").description("生产环境")));
  }

  /** 分组配置 - 只显示竞价接口 */
  @Bean
  public GroupedOpenApi openrtbApi() {
    return GroupedOpenApi.builder().group("OpenRTB").pathsToMatch("/openrtb/bid").build();
  }
}
