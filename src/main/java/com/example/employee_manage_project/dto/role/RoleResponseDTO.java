package com.example.employee_manage_project.dto.role;

import com.example.employee_manage_project.dto.permission.PermissionResponseDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponseDTO {
    private String name;
    private String description;
    private List<PermissionResponseDTO> permissions;
}
