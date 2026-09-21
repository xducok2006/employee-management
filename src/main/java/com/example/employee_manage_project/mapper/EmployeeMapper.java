package com.example.employee_manage_project.mapper;

import com.example.employee_manage_project.dto.employee.EmployeeRequestDTO;
import com.example.employee_manage_project.dto.employee.EmployeeResponseDTO;
import com.example.employee_manage_project.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    @Mapping(target = "department", ignore = true)
    Employee toEmployee(EmployeeRequestDTO request);
    EmployeeResponseDTO toEmployeeResponse(Employee employee);
}
