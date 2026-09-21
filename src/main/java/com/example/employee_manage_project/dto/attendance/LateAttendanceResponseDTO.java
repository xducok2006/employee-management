package com.example.employee_manage_project.dto.attendance;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LateAttendanceResponseDTO {
    private Long employeeId;
    private String employeeName;
    private Long lateCount;
}
