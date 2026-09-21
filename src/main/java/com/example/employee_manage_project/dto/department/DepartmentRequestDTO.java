package com.example.employee_manage_project.dto.department;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentRequestDTO {
    @NotBlank(message = "Tên phòng ban không được để trống")
    private String name;
    @NotNull
    private String description;
}
