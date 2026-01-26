package wake.su.zhuque.bid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 竞价服务启动类
 *
 * @author zhuque
 * @version 1.0
 */
@SpringBootApplication(scanBasePackages = "wake.su.zhuque")
public class JavaBidApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaBidApplication.class, args);
    }
}
