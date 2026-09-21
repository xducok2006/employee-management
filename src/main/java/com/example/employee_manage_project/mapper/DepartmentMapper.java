package com.example.employee_manage_project.mapper;

import com.example.employee_manage_project.dto.department.DepartmentRequestDTO;
import com.example.employee_manage_project.dto.department.DepartmentResponseDTO;
import com.example.employee_manage_project.entity.Department;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    Department toDepartment(DepartmentRequestDTO request);
    DepartmentResponseDTO toDepartmentResponse(Department department);
}
