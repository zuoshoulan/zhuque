package wake.su.zhuque.bid;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

/**
 * 竞价服务启动类 (bid-java)
 *
 * @author zhuque
 */
@SpringBootApplication(scanBasePackages = { "wake.su.zhuque.bid", "wake.su.zhuque.common" })
@MapperScan("wake.su.zhuque.dao.mapper")
public class BidJavaApplication {

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(BidJavaApplication.class);
    Environment env = app.run(args).getEnvironment();
    String port = env.getProperty("server.port", "8081");
    System.out.println("""
        ========================================
           朱雀竞价服务启动成功！
           竞价接口: http://localhost:%s/openrtb/bid
           Swagger: http://localhost:%s/swagger-ui.html
           健康检查: http://localhost:%s/actuator/health
        ========================================
        """.formatted(port, port, port));
  }
}
