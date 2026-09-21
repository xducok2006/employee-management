package com.example.employee_manage_project.dto.attendance;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttendanceCheckOutRequest {
    private Long version;
}
