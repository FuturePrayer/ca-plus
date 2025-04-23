package cn.suhoan.caplus.config;

import cn.dev33.satoken.SaManager;
import cn.suhoan.caplus.dto.ModelErrorResult;
import cn.suhoan.caplus.dto.ResultVo;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * @author FuturePrayer
 * @date 2025/4/22
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
@Slf4j
public class ApiKeyFilter implements Filter {

    private static final String REQUIRED_HEADER = "Authorization";

    private final String API_KEY;

    @Autowired
    public ApiKeyFilter(@Value("${auth.api-key:}") String apiKey) {
        if (StringUtils.hasText(apiKey)) {
            this.API_KEY = apiKey;
        } else if (StringUtils.hasText(System.getenv("CAPLUS_API_KEY"))) {
            this.API_KEY = System.getenv("CAPLUS_API_KEY");
        } else {
            this.API_KEY = null;
            log.warn("未配置api-key，将不进行鉴权。为了防止您的财产受到损失，强烈建议您配置api-key以开启鉴权。");
            log.warn("The api-key is not configured, authentication will not be performed. To prevent your property from being damaged, it is strongly recommended that you configure api-key to enable authentication.");
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (!StringUtils.hasText(API_KEY)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        if (!httpServletRequest.getRequestURI().contains("/api")){
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        
        String header = httpServletRequest.getHeader(REQUIRED_HEADER);
        if (!StringUtils.hasText(header)) {
            log.warn("请求未携带api-key，拒绝");
            servletResponse.getOutputStream().write(SaManager.getSaJsonTemplate().objectToJson(new ModelErrorResult("400", "Missing ".concat(REQUIRED_HEADER), "Forbidden: Missing required header '".concat(REQUIRED_HEADER).concat("'"))).getBytes());
        } else if (!("Bearer " + API_KEY).equals(header)) {
            log.warn("请求携带了错误的api-key，拒绝");
            servletResponse.getOutputStream().write(SaManager.getSaJsonTemplate().objectToJson(new ModelErrorResult("400", "Wrong ".concat(REQUIRED_HEADER), "Forbidden: Required header '".concat(REQUIRED_HEADER).concat("' is wrong"))).getBytes());
        }
    }
}
