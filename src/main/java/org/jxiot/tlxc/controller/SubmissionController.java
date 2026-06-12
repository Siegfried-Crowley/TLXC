package org.jxiot.tlxc.controller;

import org.jxiot.tlxc.dto.ApiResponse;
import org.jxiot.tlxc.entity.Problem;
import org.jxiot.tlxc.service.ProblemService;
import org.jxiot.tlxc.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/submissions")
@CrossOrigin
public class SubmissionController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ProblemService problemService;

    @Value("${deepseek.api.key}")
    private String apiKey;

    @PostMapping
    public ApiResponse<Map<String, Object>> submit(@RequestBody Map<String, Object> payload,
                                                   @RequestAttribute(required = false) Integer userId,
                                                   HttpServletRequest request) {
        if (userId == null) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    userId = jwtUtil.getUserIdFromToken(token);
                } catch (Exception e) {
                    return ApiResponse.error(401, "无效的 token");
                }
            }
        }
        if (userId == null) {
            return ApiResponse.error(401, "未登录或用户信息缺失");
        }

        Integer problemId = (Integer) payload.get("problemId");
        String code = (String) payload.get("code");

        // 获取题目信息
        Problem problem = problemService.getProblemById(problemId);
        if (problem == null) {
            return ApiResponse.error(404, "题目不存在");
        }

        // 构造 prompt
        String prompt = "你是一个编程评测助手。请判断以下代码是否正确地解决了题目要求。如果正确，输出“正确”；如果不正确，指出错误并给出修改建议。\n\n"
                + "题目描述：\n" + problem.getDescription() + "\n"
                + "输入格式：" + (problem.getInputDescription() != null ? problem.getInputDescription() : "无") + "\n"
                + "输出格式：" + (problem.getOutputDescription() != null ? problem.getOutputDescription() : "无") + "\n"
                + "示例：" + (problem.getExamples() != null ? problem.getExamples() : "无") + "\n\n"
                + "用户代码：\n```\n" + code + "\n```\n\n"
                + "请给出判断结果和简要分析。";

        String analysis = callDeepSeek(prompt);
        if (analysis == null) {
            return ApiResponse.error(500, "AI 评测服务暂时不可用，请稍后重试");
        }

        // 简单判断正确性
        boolean correct = analysis.contains("正确") && !analysis.contains("不正确");

        Map<String, Object> result = new HashMap<>();
        result.put("analysis", analysis);
        result.put("correct", correct);
        result.put("problemId", problemId);
        // 可选：保存提交记录到数据库，这里省略

        return ApiResponse.success(result);
    }

    private String callDeepSeek(String prompt) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "deepseek-chat");
        requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt)));
        requestBody.put("stream", false);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://api.deepseek.com/v1/chat/completions",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
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