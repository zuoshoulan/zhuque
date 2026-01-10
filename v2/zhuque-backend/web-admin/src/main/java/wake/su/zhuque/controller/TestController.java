package wake.su.zhuque.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wake.su.zhuque.common.security.util.PasswordGenerator;
import wake.su.zhuque.common.security.util.PasswordUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 */
@Tag(name = "测试")
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Operation(summary = "生成今日密码（规则：yyyyMMdd + 手机号）")
    @GetMapping("/gen-password")
    public Map<String, String> generatePassword(String phone) {
        if (phone == null || phone.isEmpty()) {
            phone = "13800138001"; // 默认测试手机号
        }

        String password = PasswordGenerator.generate(phone);
        String hash = PasswordUtil.encode(password);

        Map<String, String> result = new HashMap<>();
        result.put("phone", phone);
        result.put("password", password);
        result.put("hash", hash);
        result.put("date", java.time.LocalDate.now().toString());

        return result;
    }
}
