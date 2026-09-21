package com.example.employee_manage_project.dto.employee;

import com.example.employee_manage_project.entity.Department;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeRequestDTO {
    @NotBlank(message = "Tên không được để trống")
    private String fullName;
    @Email(message = "Email không đúng định dạng")
    private String email;
    @NotNull
    private String department;
}
