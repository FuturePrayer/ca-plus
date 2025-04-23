package cn.suhoan.caplus.controller;

import cn.suhoan.caplus.dto.ModelErrorResult;
import cn.suhoan.caplus.service.OpenAiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author FuturePrayer
 * @date 2025/4/21
 */
@RestController
@Slf4j
@RequestMapping
public class OpenAiController {

    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OpenAiService openAiService;

    @PostMapping("/v1/chat/completions")
    public Object completions(@RequestBody OpenAiApi.ChatCompletionRequest request, HttpServletResponse response) throws JsonProcessingException {
        log.info("创建自动补全：{}", objectMapper.writeValueAsString(request));
        Object completions = openAiService.completions(request);
        if (completions instanceof Flux<?> flux) {
            //流式返回
            SseEmitter emitter = new SseEmitter(60_000L); // 设置超时时间
            executor.execute(() -> {
                flux.doOnComplete(emitter::complete)
                        .subscribe(data -> {
                            try {
                                emitter.send(objectMapper.writeValueAsString(data));
                            } catch (Exception e) {
                                log.error("sse发送异常", e);
                                emitter.completeWithError(e);
                            }
                        });
            });

            response.setHeader("Connection", "keep-alive");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("x-accel-buffering", "no");
            return emitter;
        } else {
            //非流式返回
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String jsonResult = objectMapper.writeValueAsString(completions);
            log.info("自动补全结果：{}", jsonResult);
            return new ResponseEntity<>(jsonResult, headers, completions instanceof ModelErrorResult ? HttpStatus.BAD_REQUEST : HttpStatus.OK);
        }
    }

    @GetMapping("/v1/models")
    public Object models() throws JsonProcessingException {
        log.info("获取模型列表");
        List<Map<String, Object>> models = openAiService.models();
        log.info("模型列表，返回报文：{}", objectMapper.writeValueAsString(models));
        return models;
    }

    @PostMapping("/v1/embeddings")
    public Object embeddings(@RequestBody OpenAiApi.EmbeddingRequest<Object> request) throws JsonProcessingException {
        log.info("创建嵌入：{}", objectMapper.writeValueAsString(request));
        if (!(request.input() instanceof String) && !(request.input() instanceof Collection<?>)) {
            ModelErrorResult errorResult = new ModelErrorResult(
                    "invalid param",
                    "embedding input must be string or array",
                    "embedding input must be string or array"
            );
        }
        Object embeddings = openAiService.embeddings(request);
        log.info("嵌入结果：{}", objectMapper.writeValueAsString(embeddings));
        return embeddings;
    }
}
