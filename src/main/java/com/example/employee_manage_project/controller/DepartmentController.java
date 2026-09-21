package com.example.employee_manage_project.controller;

import com.example.employee_manage_project.dto.ApiResponse;
import com.example.employee_manage_project.dto.department.DepartmentRequestDTO;
import com.example.employee_manage_project.dto.department.DepartmentResponseDTO;
import com.example.employee_manage_project.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.Builder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Builder
@RestController
@RequestMapping("/department")
@SecurityRequirement(name = "Token")
public class DepartmentController {
    private DepartmentService departmentService;

    @Operation(
            summary = "Thêm phòng ban mới"
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
    public ResponseEntity<ApiResponse<DepartmentResponseDTO>> create(@Valid @RequestBody DepartmentRequestDTO request)
    {
        ApiResponse<DepartmentResponseDTO> response = new ApiResponse<>(200,"Call api successfully",departmentService.create(request));
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentResponseDTO>> getById(@PathVariable Long id)
    {
        ApiResponse<DepartmentResponseDTO> response = new ApiResponse<>(200,"Call api successfully",departmentService.getById(id));
        return ResponseEntity.ok(response);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<DepartmentResponseDTO>> update(@RequestBody DepartmentRequestDTO request, @PathVariable Long id)
    {
        ApiResponse<DepartmentResponseDTO> response = new ApiResponse<>(200,"Call api successfully",departmentService.update(request, id));
        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "Xem danh sách phòng ban"
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
    public ResponseEntity<ApiResponse<List<DepartmentResponseDTO>>> getAll()
    {
        ApiResponse<List<DepartmentResponseDTO>> response = new ApiResponse<>(200,"Call api successfully", departmentService.getAll());
        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "Xoá phòng ban"
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
        departmentService.delete(id);
        ApiResponse<Void> response = new ApiResponse<>(200,"Call api successfully", null);
        return ResponseEntity.ok(response);
    }
}
