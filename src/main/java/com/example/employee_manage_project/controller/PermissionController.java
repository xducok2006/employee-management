package com.example.employee_manage_project.controller;

import com.example.employee_manage_project.dto.ApiResponse;
import com.example.employee_manage_project.dto.permission.PermissionRequestDTO;
import com.example.employee_manage_project.dto.permission.PermissionResponseDTO;
import com.example.employee_manage_project.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/permission")
@SecurityRequirement(name = "Token")
public class PermissionController {
    private final PermissionService permissionService;
    @Operation(
            summary = "Thêm Permission mới"
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
    public ResponseEntity<ApiResponse<PermissionResponseDTO>> create(@Valid @RequestBody PermissionRequestDTO request)
    {
        ApiResponse<PermissionResponseDTO> response = new ApiResponse<>(200,"Call api successfully",permissionService.create(request));
        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "Xem danh sách Permission"
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
    public ResponseEntity<ApiResponse<List<PermissionResponseDTO>>> getAll()
    {
        ApiResponse<List<PermissionResponseDTO>> response = new ApiResponse<>(200,"Call api successfully",permissionService.getAll());
        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "Xoá Permission"
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
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id)
    {
        permissionService.delete(id);
        ApiResponse<Void> response = new ApiResponse<>(200,"Call successfully",null);
        return ResponseEntity.ok(response);
    }
}
