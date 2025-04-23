package cn.suhoan.caplus.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author FuturePrayer
 * @date 2025/4/21
 */
@AllArgsConstructor
@Getter
public enum SupplierType {
    OPENAI_STYLE(0, "OpenAI兼容");

    private final int code;
    private final String name;
    
    public static SupplierType getByCode(int code) {
        for (SupplierType value : values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        return null;
    }
}
