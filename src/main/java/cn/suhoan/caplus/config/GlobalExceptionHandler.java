package cn.suhoan.caplus.config;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.util.SaResult;
import cn.suhoan.caplus.dto.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * @author FuturePrayer
 * @date 2025/4/21
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final HttpHeaders HEADERS;

    static {
        HEADERS = new HttpHeaders();
        HEADERS.add("Content-Type", "application/json");
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<Object> handleOptimisticLockingException(ObjectOptimisticLockingFailureException ex, WebRequest request) {
        log.error("乐观锁检核未通过！", ex);
        return new ResponseEntity<>(
                SaManager.getSaJsonTemplate().objectToJson(ResultVo.error("409", "系统繁忙，请稍后重试")),
                HEADERS,
//                HttpStatus.CONFLICT
                HttpStatus.OK
        );
    }

    @ExceptionHandler(NotLoginException.class)
    public ResponseEntity<Object> handleNotLoginException(NotLoginException ex, WebRequest request) {
        log.error("用户尚未登陆！", ex);
        return new ResponseEntity<>(
                SaManager.getSaJsonTemplate().objectToJson(ResultVo.error("401", "请先登录")),
                HEADERS,
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {
        log.error("出现未知异常！", ex);
        return new ResponseEntity<>(
                SaManager.getSaJsonTemplate().objectToJson(ResultVo.error("系统开小差了~~")),
                HEADERS,
//                HttpStatus.INTERNAL_SERVER_ERROR
                HttpStatus.OK
        );
    }
}
