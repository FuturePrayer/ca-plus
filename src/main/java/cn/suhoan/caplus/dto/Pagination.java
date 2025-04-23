package cn.suhoan.caplus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 分页查询
 *
 * @author FuturePrayer
 * @date 2025/4/21
 */
public record Pagination(
        @JsonProperty(defaultValue = "0")
        int page,
        @JsonProperty(defaultValue = "10")
        int size,
        @JsonProperty(defaultValue = "id")
        String sort) {
}
