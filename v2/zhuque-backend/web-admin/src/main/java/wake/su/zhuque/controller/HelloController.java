package wake.su.zhuque.controller;

import wake.su.zhuque.common.core.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Hello World控制器
 */
@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/hello")
    public Result<Map<String, Object>> hello() {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Hello, 朱雀广告平台！");
        data.put("time", LocalDateTime.now());
        data.put("version", "2.0.0");
        return Result.success(data);
    }

    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("系统运行正常", "OK");
    }

}
