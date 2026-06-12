package org.jxiot.tlxc.controller;

import org.jxiot.tlxc.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin
public class AiController {

    @Value("${deepseek.api.key}")
    private String apiKey;

    @Value("${deepseek.api.url:https://api.deepseek.com/v1/chat/completions}")
    private String apiUrl;

    @PostMapping("/analyze")
    public ApiResponse<String> analyze(@RequestBody Map<String, String> payload) {
        String code = payload.get("code");
        String question = payload.get("question");

        if (code == null || code.trim().isEmpty()) {
            return ApiResponse.error(400, "代码不能为空");
        }
        if (question == null || question.trim().isEmpty()) {
            return ApiResponse.error(400, "问题不能为空");
        }

        String prompt = "你是一名编程专家。请分析以下代码并回答用户的问题。\n\n"
                + "### 代码\n```\n" + code + "\n```\n\n"
                + "### 用户问题\n" + question + "\n\n"
                + "请给出详细、专业的分析和回答。";

        String answer = callDeepSeek(prompt);
        if (answer == null) {
            return ApiResponse.error(500, "AI 服务调用失败，请稍后重试");
        }
        return ApiResponse.success(answer);
    }

    private String callDeepSeek(String prompt) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "deepseek-chat");
        requestBody.put("messages", List.of(
                Map.of("role", "user", "content", prompt)
        ));
        requestBody.put("stream", false);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}