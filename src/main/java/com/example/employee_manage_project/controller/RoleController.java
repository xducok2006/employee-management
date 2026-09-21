package com.example.employee_manage_project.controller;

import com.example.employee_manage_project.dto.ApiResponse;
import com.example.employee_manage_project.dto.role.RoleRequestDTO;
import com.example.employee_manage_project.dto.role.RoleResponseDTO;
import com.example.employee_manage_project.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
@SecurityRequirement(name = "Token")
public class RoleController {
    private final RoleService roleService;
    @Operation(
            summary = "Thêm Role mới"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Thêm thành công"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Chưa đăng nhập"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "User không đủ quyền"
            )
    })
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<RoleResponseDTO>> create(@Valid @RequestBody RoleRequestDTO request)
    {
        ApiResponse<RoleResponseDTO> response = new ApiResponse<>(200,"Call api successfully",roleService.create(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @Operation(
            summary = "Xem danh sách Role"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Lấy thành công"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Chưa đăng nhập"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "User không đủ quyền"
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleResponseDTO>>> getAll()
    {
        ApiResponse<List<RoleResponseDTO>> response = new ApiResponse<>(200,"Call api successfully",roleService.getAll());
        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "Xoá Role"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Xoá thành công"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Chưa đăng nhập"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Không có quyền xoá tài khoản này"
            )
    })
    @DeleteMapping("/{name}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable @RequestBody String name)
    {
        roleService.delete(name);
        ApiResponse<Void> response = new ApiResponse<>(200,"Call api successfully",null);
        return ResponseEntity.ok(response);

    }
}
