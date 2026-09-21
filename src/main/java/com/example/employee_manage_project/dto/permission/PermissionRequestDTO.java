package com.example.employee_manage_project.dto.permission;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionRequestDTO {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
}
