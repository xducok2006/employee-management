package com.example.employee_manage_project.dto.permission;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PermissionResponseDTO {
    private String name;
    private String description;
}
