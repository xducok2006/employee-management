package com.example.employee_manage_project.mapper;

import com.example.employee_manage_project.dto.user.UserRequestDTO;
import com.example.employee_manage_project.dto.user.UserResponseDTO;
import com.example.employee_manage_project.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password",ignore = true)
    User toUser(UserRequestDTO request);
    UserResponseDTO toUserResponse(User user);
}
