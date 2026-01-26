package wake.su.zhuque.bid;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 竞价服务启动类 (bid-java)
 *
 * @author zhuque
 */
@SpringBootApplication(scanBasePackages = "wake.su.zhuque.bid")
@MapperScan("wake.su.zhuque.dao.mapper")
public class BidJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(BidJavaApplication.class, args);
    }
}
