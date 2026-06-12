package org.jxiot.tlxc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AiService {

    @Value("${deepseek.api.key}")
    private String apiKey;

    @Value("${deepseek.api.url}")
    private String apiUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String analyzeCode(String code, String question) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(apiUrl);

            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "deepseek-chat");

            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", "请分析以下代码并回答问题：" + question + "\n\n代码：\n" + code);

            requestBody.put("messages", new Object[]{message});
            requestBody.put("temperature", 0.7);

            StringEntity entity = new StringEntity(objectMapper.writeValueAsString(requestBody));
            httpPost.setEntity(entity);

            CloseableHttpResponse response = httpClient.execute(httpPost);
            String responseBody = EntityUtils.toString(response.getEntity());

            JsonNode jsonNode = objectMapper.readTree(responseBody);
            String aiResponse = jsonNode.get("choices").get(0).get("message").get("content").asText();

            httpClient.close();
            return aiResponse;

        } catch (Exception e) {
            return "AI分析失败: " + e.getMessage();
        }
    }
}
