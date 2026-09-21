package com.example.employee_manage_project.dto.login;

import com.example.employee_manage_project.dto.employee.EmployeeRequestDTO;
import com.example.employee_manage_project.dto.user.UserRequestDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDTO {
    private EmployeeRequestDTO employeeRequest;
    private UserRequestDTO userRequest;

}
