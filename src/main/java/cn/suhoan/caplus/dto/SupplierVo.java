package cn.suhoan.caplus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author FuturePrayer
 * @date 2025/4/21
 */
public record SupplierVo(
        Long id,
        //供应商类型
        @JsonProperty(defaultValue = "0")
        Integer type,
        //供应商名称
        String name,
        //接口地址
        @JsonProperty(defaultValue = "https://api.openai.com/v1")
        String baseUrl,
        //模型列表
        List<ModelVo> modelList,
        //密钥列表
        List<KeyVo> keyList
) {

    public record ModelVo(
            Long id,
            //模型名称
            String modelName,
            //后端调用时实际模型名称
            String realModelName,
            //模型类型
            @JsonProperty(defaultValue = "0")
            Integer type
    ) {
    }

    public record KeyVo(
            Long id,
            //密钥
            String apiKey,
            //权重
            @JsonProperty(defaultValue = "1")
            Integer weight
    ) {
    }
}
