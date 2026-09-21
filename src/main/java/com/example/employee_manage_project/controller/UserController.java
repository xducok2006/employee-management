package com.example.employee_manage_project.controller;

import com.example.employee_manage_project.dto.ApiResponse;
import com.example.employee_manage_project.dto.login.AuthenticateResponse;
import com.example.employee_manage_project.dto.login.LoginRequest;
import com.example.employee_manage_project.dto.user.UserRequestDTO;
import com.example.employee_manage_project.dto.user.UserResponseDTO;
import com.example.employee_manage_project.entity.RefreshToken;
import com.example.employee_manage_project.entity.User;
import com.example.employee_manage_project.service.JwtService;
import com.example.employee_manage_project.service.RefreshTokenService;
import com.example.employee_manage_project.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@SecurityRequirement(name = "Token")
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

//    @PostMapping("/create")
//    public ResponseEntity<ApiResponse<UserResponseDTO>> create(@Valid @RequestBody UserRequestDTO request)
//    {
//        ApiResponse<UserResponseDTO> response = new ApiResponse<>(200,"Call api successfully",userService.create(request));
//        return ResponseEntity.ok(response);
//    }

    @Operation(
            summary = "Xem danh sách tài khoản nhân viên phòng ban"
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
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAll()
    {
        ApiResponse<List<UserResponseDTO>> response = new ApiResponse<>(200,"Call api successfully",userService.getAll());
        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "Xoá tài khoản"
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
        userService.delete(id);
        ApiResponse<Void> response = new ApiResponse<>(200,"Call api successfully",null);
        return ResponseEntity.ok(response);
    }

}
