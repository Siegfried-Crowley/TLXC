package org.jxiot.tlxc.controller;

import org.jxiot.tlxc.dto.ApiResponse;
import org.jxiot.tlxc.service.DeepSeekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin
public class AiController {

    @Autowired
    private DeepSeekService deepSeekService;

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

        String answer = deepSeekService.callDeepSeek(prompt);
        if (answer == null) {
            return ApiResponse.error(500, "AI 服务调用失败，请稍后重试");
        }
        return ApiResponse.success(answer);
    }
}
