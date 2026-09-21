package com.example.employee_manage_project.mapper;

import com.example.employee_manage_project.dto.role.RoleRequestDTO;
import com.example.employee_manage_project.dto.role.RoleResponseDTO;
import com.example.employee_manage_project.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequestDTO request);
    RoleResponseDTO toRoleResponse(Role role);
}
