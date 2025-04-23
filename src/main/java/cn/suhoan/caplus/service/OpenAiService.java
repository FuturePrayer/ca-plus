package cn.suhoan.caplus.service;

import cn.suhoan.caplus.dto.ModelErrorResult;
import cn.suhoan.caplus.entity.BaseModel;
import cn.suhoan.caplus.entity.ModelKey;
import cn.suhoan.caplus.entity.Supplier;
import cn.suhoan.caplus.enums.ModelType;
import cn.suhoan.caplus.enums.SupplierType;
import cn.suhoan.caplus.repository.BaseModelRepository;
import cn.suhoan.caplus.repository.ModelKeyRepository;
import cn.suhoan.caplus.repository.SupplierRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author FuturePrayer
 * @date 2025/4/21
 */
@Slf4j
@Service
public class OpenAiService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private BaseModelRepository baseModelRepository;

    @Autowired
    private ModelKeyRepository modelKeyRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public Object completions(OpenAiApi.ChatCompletionRequest request) {
        String model = request.model();
        List<BaseModel> modelList = baseModelRepository.findAllByModelNameAndType(model, ModelType.CHAT_COMPLETION.getCode());
        if (modelList == null || modelList.isEmpty()) {
            log.error("未找到{}模型", model);
            return new ModelErrorResult(
                    "unknown_model",
                    "Unknown model: " + model,
                    "Unknown model: " + model
            );
        }
        BaseModel baseModel = modelList.get(new Random().nextInt(modelList.size()));
        model = StringUtils.hasText(baseModel.getRealModelName()) ? baseModel.getRealModelName() : model;

        Optional<Supplier> supplierOptional = supplierRepository.findById(baseModel.getSupplierId());
        if (supplierOptional.isEmpty()) {
            log.error("未找到{}供应商", baseModel.getSupplierId());
            return new ModelErrorResult(
                    "unknown_model",
                    "Unknown model: " + model,
                    "Unknown model: " + model
            );
        }
        Supplier supplier = supplierOptional.get();
        if (!supplier.getEnabled()) {
            log.error("供应商{}已禁用", baseModel.getSupplierId());
            return new ModelErrorResult(
                    "unknown_model",
                    supplier.getName() + "is disabled",
                    supplier.getName() + "is disabled"
            );
        }

        SupplierType supplierType = SupplierType.getByCode(supplier.getType());
        if (SupplierType.OPENAI_STYLE == supplierType) {
            //OPENAI
            List<ModelKey> modelKeys = modelKeyRepository.findAllBySupplierIdOrderByWeight(baseModel.getSupplierId());
            if (modelKeys == null || modelKeys.isEmpty()) {
                log.error("供应商{}没有可用的key", baseModel.getSupplierId());
                return new ModelErrorResult(
                        "unknown_model",
                        supplier.getName() + "has no available key",
                        supplier.getName() + "has no available key"
                );
            }
            ModelKey modelKey = null;
            if (modelKeys.size() == 1) {
                modelKey = modelKeys.get(0);
            } else {
                IntSummaryStatistics summaryStatistics = modelKeys.stream().map(ModelKey::getWeight).collect(Collectors.summarizingInt(Integer::intValue));
                long sum = summaryStatistics.getSum();
                long random = new Random().nextLong(sum);
                long weight = 0;
                for (ModelKey bo : modelKeys) {
                    modelKey = bo;
                    weight += bo.getWeight();
                    if (weight >= random) {
                        break;
                    }
                }
            }

            request = new OpenAiApi.ChatCompletionRequest(
                    request.messages(),
                    model,
                    request.store(),
                    request.metadata(),
                    request.frequencyPenalty(),
                    request.logitBias(),
                    request.logprobs(),
                    request.topLogprobs(),
                    request.maxTokens(),
                    request.maxCompletionTokens(),
                    request.n(),
                    request.outputModalities(),
                    request.audioParameters(),
                    request.presencePenalty(),
                    request.responseFormat(),
                    request.seed(),
                    request.serviceTier(),
                    request.stop(),
                    request.stream() != null && request.stream(),
                    request.streamOptions(),
                    request.temperature(),
                    request.topP(),
                    request.tools(),
                    request.toolChoice(),
                    request.parallelToolCalls(),
                    request.user(),
                    request.reasoningEffort()
            );
            OpenAiApi openAiApi = OpenAiApi.builder()
                    .baseUrl(supplier.getBaseUrl())
                    .apiKey(modelKey.getApiKey())
                    .completionsPath("/chat/completions")
                    .build();
            if (request.stream()) {
                return openAiApi.chatCompletionStream(request);
            } else {
                return openAiApi.chatCompletionEntity(request);
            }
        } else {
            log.error("未知供应商类型{}", supplierType);
            return new ModelErrorResult(
                    "unknown_model",
                    "Unknown model: " + model,
                    "Unknown model: " + model
            );
        }
    }

    public List<Map<String, Object>> models() {
        List<Supplier> supplierList = supplierRepository.findAllByEnabled(true);
        if (supplierList == null || supplierList.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, Supplier> supplierMap = supplierList.stream().collect(Collectors.toMap(Supplier::getId, o -> o));

        List<BaseModel> baseModels = baseModelRepository.findAllByTypeAndSupplierIdIn(ModelType.CHAT_COMPLETION.getCode(), supplierMap.keySet());
        if (baseModels == null || baseModels.isEmpty()) {
            return Collections.emptyList();
        }
        Map<String, Optional<BaseModel>> optionalMap = baseModels.stream().collect(
                Collectors.groupingBy(
                        BaseModel::getModelName,
                        Collectors.minBy(Comparator.comparing(BaseModel::getId))
                )
        );
        return optionalMap.values().stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted(Comparator.comparing(BaseModel::getModelName))
                .map(baseModel -> Map.<String, Object>of("id", baseModel.getModelName(), "object", "model", "created", baseModel.getCreateTime().toEpochSecond(ZoneOffset.UTC), "owned_by", supplierMap.get(baseModel.getSupplierId()).getName()))
                .toList();
    }

    public Object embeddings(OpenAiApi.EmbeddingRequest<Object> request) {
        String model = request.model();
        List<BaseModel> modelList = baseModelRepository.findAllByModelNameAndType(model, ModelType.EMBEDDING.getCode());
        if (modelList == null || modelList.isEmpty()) {
            log.error("未找到{}模型", model);
            return new ModelErrorResult(
                    "unknown_model",
                    "Unknown model: " + model,
                    "Unknown model: " + model
            );
        }

        BaseModel baseModel = modelList.get(new Random().nextInt(modelList.size()));
        model = StringUtils.hasText(baseModel.getRealModelName()) ? baseModel.getRealModelName() : model;

        Optional<Supplier> supplierOptional = supplierRepository.findById(baseModel.getSupplierId());
        if (supplierOptional.isEmpty()) {
            log.error("未找到{}供应商", baseModel.getSupplierId());
            return new ModelErrorResult(
                    "unknown_model",
                    "Unknown model: " + model,
                    "Unknown model: " + model
            );
        }
        Supplier supplier = supplierOptional.get();
        if (!supplier.getEnabled()) {
            log.error("供应商{}已禁用", baseModel.getSupplierId());
            return new ModelErrorResult(
                    "unknown_model",
                    supplier.getName() + "is disabled",
                    supplier.getName() + "is disabled"
            );
        }

        SupplierType supplierType = SupplierType.getByCode(supplier.getType());
        if (SupplierType.OPENAI_STYLE == supplierType) {
            //OPENAI
            List<ModelKey> modelKeys = modelKeyRepository.findAllBySupplierIdOrderByWeight(baseModel.getSupplierId());
            if (modelKeys == null || modelKeys.isEmpty()) {
                log.error("供应商{}没有可用的key", baseModel.getSupplierId());
                return new ModelErrorResult(
                        "unknown_model",
                        supplier.getName() + "has no available key",
                        supplier.getName() + "has no available key"
                );
            }
            ModelKey modelKey = null;
            if (modelKeys.size() == 1) {
                modelKey = modelKeys.get(0);
            } else {
                IntSummaryStatistics summaryStatistics = modelKeys.stream().map(ModelKey::getWeight).collect(Collectors.summarizingInt(Integer::intValue));
                long sum = summaryStatistics.getSum();
                long random = new Random().nextLong(sum);
                long weight = 0;
                for (ModelKey bo : modelKeys) {
                    modelKey = bo;
                    weight += bo.getWeight();
                    if (weight >= random) {
                        break;
                    }
                }
            }

            request = new OpenAiApi.EmbeddingRequest<>(
                    request.input(),
                    model,
                    request.encodingFormat(),
                    request.dimensions(),
                    request.user()
            );
            OpenAiApi openAiApi = OpenAiApi.builder()
                    .baseUrl(supplier.getBaseUrl())
                    .apiKey(modelKey.getApiKey())
                    .embeddingsPath("/embeddings")
                    .build();
            return openAiApi.embeddings(request);
        } else {
            log.error("未知供应商类型{}", supplierType);
            return new ModelErrorResult(
                    "unknown_model",
                    "Unknown model: " + model,
                    "Unknown model: " + model
            );
        }
    }
}
