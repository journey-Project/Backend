package com.project.Journey.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class DiscordLogNotifier {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${discord.webhook-url}")
    private String webhookUrl;

    public void sendLog(String message) {
        sendLog(message, false);
    }

    public void sendLog(String message, boolean isError) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            System.out.println("⚠️ Webhook URL이 설정되어 있지 않습니다.");
            return;
        }

        // 디스코드 메시지 최대 길이 제한: 2000자
        if (message.length() > 1900) {
            message = message.substring(0, 1900) + "...(중략)";
        }

        String emoji = isError ? "❌" : "✅";
        String payload = String.format("{\"content\": \"%s %s\"}", emoji, message);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(payload, headers);

        try {
            restTemplate.postForEntity(webhookUrl, request, String.class);
        } catch (Exception e) {
            System.out.println("❌ 디스코드 전송 실패: " + e.getMessage());
        }
    }
}
