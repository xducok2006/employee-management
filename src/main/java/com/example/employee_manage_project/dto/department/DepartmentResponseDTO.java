package com.example.employee_manage_project.dto.department;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentResponseDTO {
    private String name;
    private String description;
}
