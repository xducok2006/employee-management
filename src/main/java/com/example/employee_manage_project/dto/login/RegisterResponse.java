package com.example.employee_manage_project.dto.login;

import com.example.employee_manage_project.dto.employee.EmployeeResponseDTO;
import com.example.employee_manage_project.dto.user.UserResponseDTO;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {
    private UserResponseDTO userResponseDTO;
    private EmployeeResponseDTO employeeResponseDTO;
}
