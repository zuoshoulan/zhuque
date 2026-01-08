package wake.su.zhuque.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试控制器
 * <p>
 * 用于生成BCrypt密码等测试功能
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/api/test")
public class TestController {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 生成BCrypt密码
     *
     * @param password 原始密码
     * @return 加密后的密码
     */
    @GetMapping("/encrypt")
    public String encryptPassword(@RequestParam(defaultValue = "123456") String password) {
        String encoded = passwordEncoder.encode(password);
        log.info("生成BCrypt密码: {} -> {}", password, encoded);
        return "原始密码: " + password + "\n加密密码: " + encoded;
    }
}
