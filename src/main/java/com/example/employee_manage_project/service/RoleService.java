package com.example.employee_manage_project.service;

import com.example.employee_manage_project.dto.role.RoleRequestDTO;
import com.example.employee_manage_project.dto.role.RoleResponseDTO;
import com.example.employee_manage_project.entity.Permission;
import com.example.employee_manage_project.entity.Role;
import com.example.employee_manage_project.exception.HandleAlreadyExists;
import com.example.employee_manage_project.exception.HandleNotFound;
import com.example.employee_manage_project.mapper.RoleMapper;
import com.example.employee_manage_project.repository.PermissionRepository;
import com.example.employee_manage_project.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final PermissionRepository permissionRepository;
    @PreAuthorize("hasRole('ADMIN')")
    public RoleResponseDTO create(RoleRequestDTO request)
    {
        if(roleRepository.existsByName(request.getName()))
            throw new HandleAlreadyExists("Role đã tồn tại");
        Role role =  roleMapper.toRole(request);
        List<Permission> permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));
        roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }
    @PreAuthorize("hasRole('ADMIN')")
    public List<RoleResponseDTO> getAll()
    {
        List<Role> roles = roleRepository.findAll();
        return roles.stream().map(roleMapper::toRoleResponse).toList();
    }
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(String role)
    {
        roleRepository.findById(role).orElseThrow(()-> new  HandleNotFound("Không tìm thấy role"));
        roleRepository.deleteById(role);
    }
}
