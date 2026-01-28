package wake.su.zhuque;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 朱雀广告平台启动类
 */
@SpringBootApplication(scanBasePackages = "wake.su.zhuque")
@MapperScan("wake.su.zhuque.dao.mapper")
public class WebAdminApplication {

  public static void main(String[] args) {
    SpringApplication.run(WebAdminApplication.class, args);
    System.out.println("""

        ========================================
           朱雀广告平台启动成功！
           访问地址: http://localhost:8080
           健康检查: http://localhost:8080/actuator/health
        ========================================
        """);
  }

}
