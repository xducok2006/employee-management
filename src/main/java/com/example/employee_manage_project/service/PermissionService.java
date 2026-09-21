package com.example.employee_manage_project.service;

import com.example.employee_manage_project.dto.permission.PermissionRequestDTO;
import com.example.employee_manage_project.dto.permission.PermissionResponseDTO;
import com.example.employee_manage_project.entity.Permission;
import com.example.employee_manage_project.mapper.PermissionMapper;
import com.example.employee_manage_project.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;
    @PreAuthorize("hasRole('ADMIN')")
    public PermissionResponseDTO create(PermissionRequestDTO request)
    {
        Permission permission = permissionMapper.toPermission(request);
        permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }
    @PreAuthorize("hasRole('ADMIN')")
    public List<PermissionResponseDTO> getAll()
    {
        List<Permission> permissions = permissionRepository.findAll();
        return permissions.stream().map(permissionMapper::toPermissionResponse).toList();
    }
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Long permission)
    {
        permissionRepository.deleteById(permission);
    }

}
