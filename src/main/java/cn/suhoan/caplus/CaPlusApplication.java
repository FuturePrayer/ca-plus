package cn.suhoan.caplus;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author FuturePrayer
 * @date 2025/4/21
 */
@SpringBootApplication
public class CaPlusApplication {

    public static void main(String[] args) {
        System.out.println("""
                        您可以通过以下任一方式使用Web控制界面的自定义凭证进行身份验证：
                        1. 设置 YAML 配置文件中的 auth.username 和 auth.password 字段（优先级更高），
                        2. 或者配置 CAPLUS_USERNAME 和 CAPLUS_PASSWORD 环境变量（优先级较低，推荐）。
                        如果未提供任何配置，系统将自动生成随机凭证并将其打印在日志中。
                """);
        System.out.println("""
                        您还可通过以下方式配置自定义API Key：
                        1. YAML配置文件：添加 auth.api-key 配置项（优先级更高）
                        2. 环境变量：设定 CAPLUS_API_KEY（优先级较低，推荐）
                        未配置时，系统将自动禁用模型接口的Authorization鉴权校验。
                """);
        System.out.println("""
                        You can authenticate with custom credentials for the web control interface by either:
                        1. Setting the auth.username and auth.password fields in the YAML configuration file (higher priority),
                        2. Or configuring the CAPLUS_USERNAME and CAPLUS_PASSWORD environment variables (lower priority, recommended).
                        If neither configuration is provided, the system will automatically generate random credentials and print them in the log.
                """);
        System.out.println("""
                        To use a custom API key, either:
                        1. Set the auth.api-key field in the YAML configuration file (higher priority),
                        2. Or set the CAPLUS_API_KEY environment variable (lower priority, recommended).
                        If neither parameter is configured, authorization checks will be disabled for model API calls.
                """);
        SpringApplication.run(CaPlusApplication.class, args);
    }

}
