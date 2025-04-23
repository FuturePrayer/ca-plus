package cn.suhoan.caplus.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author FuturePrayer
 * @date 2025/4/21
 */
@AllArgsConstructor
@Getter
public enum ModelType {
    CHAT_COMPLETION(0, "聊天补全模型"),
    EMBEDDING(1, "嵌入模型"),
    TXT_TO_IMG(2, "文生图模型");

    private final int code;
    private final String name;
}
