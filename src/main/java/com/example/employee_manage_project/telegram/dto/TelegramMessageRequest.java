package com.example.employee_manage_project.telegram.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TelegramMessageRequest {
    private String chat_id;
    private String text;
}
