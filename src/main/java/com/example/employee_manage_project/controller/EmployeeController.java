package com.example.employee_manage_project.controller;

import com.example.employee_manage_project.dto.ApiResponse;
import com.example.employee_manage_project.dto.employee.EmployeeRequestDTO;
import com.example.employee_manage_project.dto.employee.EmployeeResponseDTO;
import com.example.employee_manage_project.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
@SecurityRequirement(name = "Token")
public class EmployeeController {
    private final EmployeeService employeeService;
//    @PostMapping("/create")
//    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> create(@Valid @RequestBody EmployeeRequestDTO request)
//    {
//        ApiResponse<EmployeeResponseDTO> response = new ApiResponse<>(200,"Call api successfully",employeeService.create(request));
//        return ResponseEntity.ok(response);
//    }
    @Operation(
            summary = "Xem danh sách nhân viên phòng ban"
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
    public ResponseEntity<ApiResponse<List<EmployeeResponseDTO>>> getAll()
    {
        ApiResponse<List<EmployeeResponseDTO>> response = new ApiResponse<>(200,"Call api successfully",employeeService.getAll());
        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "Cập nhât thông tin nhân viên"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Cập nhật thành công"
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
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> update(@PathVariable Long id,@Valid @RequestBody EmployeeRequestDTO request)
    {
        ApiResponse<EmployeeResponseDTO> response = new ApiResponse<>(200,"Call api successfully",employeeService.update(id,request));
        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "Xem thông tin bản thân"
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
    @GetMapping("/myInfo")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> getMyInfo()
    {
        ApiResponse<EmployeeResponseDTO> response = new ApiResponse<>(200,"Call api successfully",employeeService.getMyInfo());
        return ResponseEntity.ok(response);
    }
//    @DeleteMapping("/{id}")
//    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id)
//    {
//        employeeService.delete(id);
//        ApiResponse<Void> response = new ApiResponse<>(200,"Call api successfully", null);
//        return ResponseEntity.ok(response);
//    }
}
