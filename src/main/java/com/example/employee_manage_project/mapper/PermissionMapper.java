package com.example.employee_manage_project.mapper;

import com.example.employee_manage_project.dto.permission.PermissionRequestDTO;
import com.example.employee_manage_project.dto.permission.PermissionResponseDTO;
import com.example.employee_manage_project.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequestDTO request);
    PermissionResponseDTO toPermissionResponse(Permission permission);
}
