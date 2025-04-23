package cn.suhoan.caplus.dto;

/**
 * @author FuturePrayer
 * @date 2025/4/21
 */
public record ResultVo<T>(
        String code,
        String msg,
        T data
) {
    public static final String SUCCESS = "0";

    public static final String DEFAULT_FAILURE = "-1";

    public static <T> ResultVo<T> ok(T data) {
        return new ResultVo<>(SUCCESS, null, data);
    }

    public static <T> ResultVo<T> ok() {
        return ResultVo.ok(null);
    }

    public static <T> ResultVo<T> error(String code, String msg) {
        return new ResultVo<>(code, msg, null);
    }

    public static <T> ResultVo<T> error(String msg) {
        return ResultVo.error(DEFAULT_FAILURE, msg);
    }
}
