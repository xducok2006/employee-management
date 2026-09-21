package com.example.employee_manage_project.dto.user;

import com.example.employee_manage_project.dto.role.RoleResponseDTO;
import com.example.employee_manage_project.entity.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
public class UserResponseDTO {
    private Long userId;
    private String username;
    private List<RoleResponseDTO> roles;
    private Long employeeId;

}
