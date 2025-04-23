package cn.suhoan.caplus.dto;

/**
 * @author FuturePrayer
 * @date 2025/4/22
 */
public record PageVo<T> (
        int totalPages,
        long totalElements,
        T data
) {
}
