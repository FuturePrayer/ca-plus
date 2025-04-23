package cn.suhoan.caplus.service;

import cn.dev33.satoken.secure.SaSecureUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * @author FuturePrayer
 * @date 2025/4/22
 */
@Service
@Slf4j
public class UserService {

    private final String username;

    private final String password;

    @Autowired
    public UserService(@Value("${auth.username:}") String username, @Value("${auth.password:}") String password) {
        if (StringUtils.hasText(username)) {
            this.username = username;
        } else if (StringUtils.hasText(System.getenv("CAPLUS_USERNAME"))) {
            this.username = System.getenv("CAPLUS_USERNAME");
        } else {
            this.username = "admin";
            log.info("未配置自定义用户名，使用默认用户名：{}", this.username);
            log.info("No custom username is configured, use the default username: {}", this.username);
        }
        if (StringUtils.hasText(password)) {
            this.password = password;
        } else if (StringUtils.hasText(System.getenv("CAPLUS_PASSWORD"))) {
            this.password = System.getenv("CAPLUS_PASSWORD");
        } else {
            this.password = SaSecureUtil.sha256(UUID.randomUUID().toString());
            log.info("未配置自定义密码，使用随机生成密码： {}", this.password);
            log.info("No custom password is configured, use a randomly generated password: {}", this.password);
        }
    }

    public boolean login(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }
}
