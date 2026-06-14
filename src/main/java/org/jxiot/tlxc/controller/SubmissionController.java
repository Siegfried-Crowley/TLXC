package org.jxiot.tlxc.controller;

import org.jxiot.tlxc.dto.ApiResponse;
import org.jxiot.tlxc.entity.Problem;
import org.jxiot.tlxc.entity.Submission;
import org.jxiot.tlxc.service.ProblemService;
import org.jxiot.tlxc.service.SubmissionService;
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

    @Autowired
    private SubmissionService submissionService;

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

        // 通过 SubmissionService 执行判题、保存提交记录、处理错题本和积分
        Submission submission = submissionService.submitCode(userId, problemId, code);

        // 构造 prompt 给 AI 分析（附带判题结果）
        String statusText = "accepted".equals(submission.getStatus()) ? "通过" :
                "wrong_answer".equals(submission.getStatus()) ? "答案错误" : "运行错误";
        String prompt = "你是一个编程评测助手。用户提交了一段代码来解决算法题。\n\n"
                + "## 题目\n"
                + problem.getTitle() + "\n\n"
                + "描述：" + problem.getDescription() + "\n"
                + "输入格式：" + (problem.getInputDescription() != null ? problem.getInputDescription() : "无") + "\n"
                + "输出格式：" + (problem.getOutputDescription() != null ? problem.getOutputDescription() : "无") + "\n"
                + "示例：" + (problem.getExamples() != null ? problem.getExamples() : "无") + "\n\n"
                + "## 用户代码\n```python\n" + code + "\n```\n\n"
                + "## 评测结果\n"
                + "状态：" + statusText + "\n"
                + "通过测试用例：" + submission.getPassedCases() + "/" + submission.getTotalCases() + "\n"
                + "运行时间：" + submission.getRuntimeMs() + "ms\n"
                + (submission.getErrorMessage() != null ? "错误信息：" + submission.getErrorMessage() + "\n" : "")
                + "\n请根据以上评测结果和代码内容，给出简要的分析和建议。";

        String analysis = callDeepSeek(prompt);

        Map<String, Object> result = new HashMap<>();
        result.put("submissionId", submission.getId());
        result.put("status", submission.getStatus());
        result.put("passedCases", submission.getPassedCases());
        result.put("totalCases", submission.getTotalCases());
        result.put("runtimeMs", submission.getRuntimeMs());
        result.put("scoreDelta", submission.getScoreDelta());
        result.put("errorMessage", submission.getErrorMessage());
        result.put("createdAt", submission.getCreatedAt());
        result.put("analysis", analysis != null ? analysis : "AI 分析服务暂时不可用，请稍后重试。");
        result.put("correct", "accepted".equals(submission.getStatus()));
        result.put("problemId", problemId);

        return ApiResponse.success(result);
    }

    @GetMapping("/my")
    public ApiResponse<List<Submission>> getMySubmissions(
            @RequestAttribute(required = false) Integer userId,
            @RequestParam(defaultValue = "100") int limit,
            HttpServletRequest request) {
        if (userId == null) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                try {
                    userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
                } catch (Exception e) {
                    return ApiResponse.error(401, "无效的 token");
                }
            }
        }
        if (userId == null) {
            return ApiResponse.error(401, "未登录或用户信息缺失");
        }
        List<Submission> submissions = submissionService.getUserSubmissions(userId, limit);
        return ApiResponse.success(submissions);
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
