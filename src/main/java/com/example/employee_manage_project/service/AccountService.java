package com.example.employee_manage_project.service;

import com.example.employee_manage_project.dto.employee.EmployeeResponseDTO;
import com.example.employee_manage_project.dto.login.LoginRequest;
import com.example.employee_manage_project.dto.login.RegisterRequestDTO;
import com.example.employee_manage_project.dto.login.RegisterResponse;
import com.example.employee_manage_project.dto.user.UserResponseDTO;
import com.example.employee_manage_project.entity.Employee;
import com.example.employee_manage_project.entity.User;
import com.example.employee_manage_project.mapper.EmployeeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final EmployeeService employeeService;
    private final UserService userService;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;
    @Transactional
    public RegisterResponse register(RegisterRequestDTO request)
    {
        Employee employee = employeeService.create(request.getEmployeeRequest());
        UserResponseDTO userResponseDTO = userService.create(request.getUserRequest(),employee);
        EmployeeResponseDTO employeeResponseDTO = employeeMapper.toEmployeeResponse(employee);
        return RegisterResponse.builder().userResponseDTO(userResponseDTO).
                employeeResponseDTO(employeeResponseDTO).build();
    }
    public User login(LoginRequest request)
    {
        User user = userService.findByAccount(request.getUsername());
        if(!passwordEncoder.matches(request.getPassword(),user.getPassword()))
            throw new RuntimeException("Sai mật khẩu");
        return user;
    }
}
