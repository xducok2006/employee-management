package com.example.employee_manage_project.service;

import com.example.employee_manage_project.dto.employee.EmployeeRequestDTO;
import com.example.employee_manage_project.dto.employee.EmployeeResponseDTO;
import com.example.employee_manage_project.entity.Employee;
import com.example.employee_manage_project.exception.HandleAlreadyExists;
import com.example.employee_manage_project.exception.HandleNotFound;
import com.example.employee_manage_project.mapper.EmployeeMapper;
import com.example.employee_manage_project.repository.DepartmentRepository;
import com.example.employee_manage_project.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final DepartmentRepository departmentRepository;
    private final JwtService jwtService;
    public Employee create(EmployeeRequestDTO request)
    {
        if(employeeRepository.existsByEmail(request.getEmail()))
            throw new HandleAlreadyExists("Email đã tồn tại");
        Employee employee = employeeMapper.toEmployee(request);
        employee.setDepartment(departmentRepository.findByName(request.getDepartment()).orElseThrow(()->new RuntimeException("Không tìm thấy phòng ban")));
        Employee savedEmployee = employeeRepository.save(employee);
        return savedEmployee;
    }
    @PreAuthorize("hasAuthority('EMPLOYEE_READ')")
    public List<EmployeeResponseDTO> getAll()
    {
        String username = jwtService.getCurrentUser();
        Employee employeeManager = employeeRepository.findEmployeeByUsername(username).orElseThrow(()->new HandleNotFound("Không tồn tại user này"));
        return employeeRepository.findAllByDepartment(employeeManager.getDepartment()).stream().map(employee ->
                EmployeeResponseDTO.builder().
                        fullName(employee.getFullName()).
                        email(employee.getEmail()).department(employee.getDepartment()).build()).toList();
    }
    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE')")
    public EmployeeResponseDTO update(Long id,EmployeeRequestDTO request)
    {
        String username = jwtService.getCurrentUser();
        Employee employeeManager = employeeRepository.findEmployeeByUsername(username).orElseThrow();
        Employee employee = employeeRepository.findById(id).orElseThrow(()->new HandleNotFound("Không tìm thấy nhân viên này"));
        if(!employeeManager.getDepartment().getName().equals(employee.getDepartment().getName()))
            throw new RuntimeException("Không có phạm vi nghiệp vụ ở phòng ban này");
        employee.setFullName(request.getFullName());
        employee.setEmail(request.getEmail());
        employee.setDepartment(departmentRepository.findByName(request.getDepartment()).orElseThrow(()->new HandleNotFound("Không tìm thấy phòng ban")));
        Employee savedEmployee = employeeRepository.save(employee);
        return EmployeeResponseDTO.builder().fullName(savedEmployee.getFullName()).email(savedEmployee.getEmail()).department(savedEmployee.getDepartment()).build();
    }
    @PreAuthorize("hasAuthority('EMPLOYEE_READ_OWN')")
    public EmployeeResponseDTO getMyInfo()
    {
        String username = jwtService.getCurrentUser();
        Employee employee = employeeRepository.findEmployeeByUsername(username).orElseThrow(()->new HandleNotFound("Không tồn tại user này"));
        return employeeMapper.toEmployeeResponse(employee);
    }
    @PreAuthorize("hasAuthority('EMPLOYEE_DELETE')")
    public void delete(Long id)
    {
        employeeRepository.findById(id).orElseThrow(()->new HandleNotFound("Không tìm thấy nhân viên này"));
        employeeRepository.deleteById(id);
    }
}
