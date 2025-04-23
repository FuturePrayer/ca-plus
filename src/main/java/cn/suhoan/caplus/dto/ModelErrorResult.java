package cn.suhoan.caplus.dto;

/**
 * @author FuturePrayer
 * @date 2025/4/21
 */
public record ModelErrorResult(
        String code,
        String message,
        String details
) {
}
