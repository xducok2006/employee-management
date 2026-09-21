package com.example.employee_manage_project.service;

import com.example.employee_manage_project.dto.user.UserRequestDTO;
import com.example.employee_manage_project.dto.user.UserResponseDTO;

import com.example.employee_manage_project.entity.Employee;
import com.example.employee_manage_project.entity.Role;
import com.example.employee_manage_project.entity.User;
import com.example.employee_manage_project.exception.HandleAlreadyExists;
import com.example.employee_manage_project.exception.HandleNotFound;
import com.example.employee_manage_project.mapper.UserMapper;
import com.example.employee_manage_project.repository.EmployeeRepository;
import com.example.employee_manage_project.repository.RoleRepository;
import com.example.employee_manage_project.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeService employeeService;

    public UserResponseDTO create(UserRequestDTO request, Employee employee)
    {
        if(userRepository.existsByUsername(request.getUsername()))
            throw new HandleAlreadyExists("Tài khoản đã tồn tại");
        List<Role> roles = roleRepository.findAllByNameIn(request.getRoles());
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmployee(employee);
        user.setRoles(new HashSet<>(roles));
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }
    public User findByAccount(String username)
    {
        return userRepository.findUserByUsername(username).orElseThrow(()->new HandleNotFound("Không tồn tại user này"));

    }
    @PreAuthorize("hasAuthority('USER_READ')")
    public List<UserResponseDTO> getAll()
    {
        List<User> users = userRepository.findAll();
        List<UserResponseDTO> userResponse = users.stream().map(userMapper::toUserResponse).toList();
        for(int i = 0;i<users.size();i++)
        {
            userResponse.get(i).setUserId(users.get(i).getId());
            userResponse.get(i).setEmployeeId(users.get(i).getEmployee().getId());
        }
        return userResponse;
    }

    @PreAuthorize("hasAuthority('USER_DELETE')")
    public void delete(Long id)
    {
        User user = userRepository.findById(id).orElseThrow(()->new HandleNotFound("Không tồn tại tài khoản này"));
        Employee employee = employeeRepository.findById(user.getEmployee().getId()).orElseThrow(()->new HandleNotFound("Không tồ tại employee này"));
        userRepository.deleteById(id);
        employeeRepository.delete(employee);
    }
}
