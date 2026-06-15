package org.jxiot.tlxc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @deprecated Use {@link DeepSeekService} instead.
 */
@Deprecated
@Service
public class AiService {

    @Autowired
    private DeepSeekService deepSeekService;

    public String analyzeCode(String code, String question) {
        String prompt = "请分析以下代码并回答问题：" + question + "\n\n代码：\n" + code;
        return deepSeekService.callDeepSeek(prompt);
    }
}
