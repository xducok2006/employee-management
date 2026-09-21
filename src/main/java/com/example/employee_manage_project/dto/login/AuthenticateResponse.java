package com.example.employee_manage_project.dto.login;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthenticateResponse {
    private String token;
}
