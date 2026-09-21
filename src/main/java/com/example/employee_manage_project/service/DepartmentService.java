package com.example.employee_manage_project.service;

import com.example.employee_manage_project.dto.department.DepartmentRequestDTO;
import com.example.employee_manage_project.dto.department.DepartmentResponseDTO;
import com.example.employee_manage_project.entity.Department;
import com.example.employee_manage_project.exception.HandleAlreadyExists;
import com.example.employee_manage_project.exception.HandleNotFound;
import com.example.employee_manage_project.mapper.DepartmentMapper;
import com.example.employee_manage_project.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    @Transactional
    @PreAuthorize("hasAuthority('DEPARTMENT_CREATE')")
    @CacheEvict(value = "departments", key = "'list'")
    public DepartmentResponseDTO create(DepartmentRequestDTO request)
    {
        if(departmentRepository.existsByName(request.getName()))
            throw new HandleAlreadyExists("Tên phòng ban đã tồn tại");
        Department department = departmentMapper.toDepartment(request);
        departmentRepository.save(department);
        return departmentMapper.toDepartmentResponse(department);
    }
    @Transactional
    @PreAuthorize("hasAuthority('DEPARTMENT_UPDATE')")
    @Caching(
        put = {@CachePut(value = "departments", key = "#id")},
            evict = {@CacheEvict(value = "departments", key = "'list'")}


    )
    public DepartmentResponseDTO update(DepartmentRequestDTO request, Long id)
    {
        Department department = departmentRepository.findById(id).orElseThrow(()->new HandleNotFound("Không tìm thấy phòng ban"));
        department.setName(request.getName());
        department.setDescription(request.getDescription());
        return departmentMapper.toDepartmentResponse(department);
    }

    @PreAuthorize("hasAuthority('DEPARTMENT_READ')")
    @Cacheable(value = "departments", key = "#id")
    public DepartmentResponseDTO getById(Long id)
    {
        return departmentMapper.
                toDepartmentResponse(departmentRepository.
                        findById(id).orElseThrow(()-> new HandleNotFound("Không tìm thấy phòng ban")));
    }
    @PreAuthorize("hasAuthority('DEPARTMENT_READ')")
    @Cacheable(value = "departments", key = "'list'")
    public List<DepartmentResponseDTO> getAll()
    {
        List<Department> departments = departmentRepository.findAll();
        return departments.stream().map(departmentMapper::toDepartmentResponse).toList();
    }
    @PreAuthorize("hasAuthority('DEPARTMENT_DELETE')")
    @CacheEvict(value = "departments", key = "#id", allEntries = true)
    public void delete(Long id)
    {
        departmentRepository.findById(id).orElseThrow(()->new HandleNotFound("Không tìm thấy phòng ban này"));
        departmentRepository.deleteById(id);
    }



}
