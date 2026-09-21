package com.example.employee_manage_project.dto.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class RoleRequestDTO {
    @NotBlank
    private String name;
    private String description;
    @NotNull
    private Set<Long> permissions;
}
