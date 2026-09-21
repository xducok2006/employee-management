package com.example.employee_manage_project.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ApiResponse<T> {
    private int errorcode;
    private String message;
    private T data;
}