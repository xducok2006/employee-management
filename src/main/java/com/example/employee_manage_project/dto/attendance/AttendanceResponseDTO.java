package com.example.employee_manage_project.dto.attendance;

import com.example.employee_manage_project.entity.Attendance;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class AttendanceResponseDTO {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private LocalDate workDate;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Attendance.Status status;
    private Long lateMinutes;
    private Long version;
}
