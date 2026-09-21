package com.example.employee_manage_project.telegram.service;

import com.example.employee_manage_project.telegram.dto.TelegramMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class TelegramService {
    private final RestTemplate restTemplate;
    @Value("${telegram.bot.token}")
    private String botToken;
    @Value("${telegram.bot.chat-id}")
    private String chatId;
    public void sendMessage(String message) {
        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";
        TelegramMessageRequest request = new TelegramMessageRequest(chatId, message);
        restTemplate.postForObject(url, request, String.class);
    }

}