package com.example.employee_manage_project.dto.employee;

import com.example.employee_manage_project.entity.Department;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EmployeeResponseDTO {
    private String fullName;
    private String email;
    private Department department;
}
