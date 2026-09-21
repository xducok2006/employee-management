package com.example.employee_manage_project.controller;

import com.example.employee_manage_project.dto.ApiResponse;
import com.example.employee_manage_project.dto.login.AuthenticateResponse;
import com.example.employee_manage_project.dto.login.LoginRequest;
import com.example.employee_manage_project.dto.login.RegisterRequestDTO;
import com.example.employee_manage_project.dto.login.RegisterResponse;
import com.example.employee_manage_project.entity.RefreshToken;
import com.example.employee_manage_project.entity.User;
import com.example.employee_manage_project.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AccountController {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final AccountService accountService;
    private final RedisLockService redisLockService;
    @Operation(
            summary = "Đăng nhập",
            description = "Trả về Access Token và thiết lập Refresh Token trong HttpOnly Cookie"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Đăng nhập thành công"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Sai tài khoản hoặc mật khẩu"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Request không hợp lệ"
            )
    })
    @PostMapping ("/login")
    public ResponseEntity<AuthenticateResponse> login(@RequestBody LoginRequest request)
    {
        User user = accountService.login(request);
        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.refreshToken(user);
        ResponseCookie responseCookie = ResponseCookie.from("refresh_token",refreshToken.getToken()).
                httpOnly(true).secure(false).path("/").maxAge(7*24*60*60).sameSite("Lax").build();
        AuthenticateResponse response = AuthenticateResponse.builder().token(accessToken).build();
        return ResponseEntity.ok().header("Set-Cookie",responseCookie.toString()).body(response);

    }
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@RequestBody RegisterRequestDTO request)
    {
        ApiResponse<RegisterResponse> response = new ApiResponse<>(200,"Register successfully", accountService.register(request));
        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "Refresh Access Token",
            description = "Cấp lại Access Token"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Cấp lại Access Token thành công"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Refresh Token không hợp lệ / hết hạn"
            )
    })
    @PostMapping("/refresh-token")
    public String refreshAccessToken(@CookieValue("refresh_token") String refreshToken)
    {
        return refreshTokenService.refreshAccessToken(refreshToken);
    }
    @Operation(
            summary = "Đăng xuất",
            description = "Vô hiệu hóa Refresh Token và xóa Refresh Token khỏi HttpOnly Cookie"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Đăng xuất thành công"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Chưa đăng nhập"
            )
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(value = "refresh_token", required = false) String token)
    {
        if(token!=null)
            refreshTokenService.deleteRefreshToken(token);
        ResponseCookie responseCookie = ResponseCookie.from("refresh_token","").
                httpOnly(true).secure(false).path("/").maxAge(0).sameSite("Lax").
                build();
        return ResponseEntity.ok().header("Set-Cookie",responseCookie.toString()).build();
    }

    @GetMapping("/lock")
    public String testLock()
    {
        boolean locked = redisLockService.tryLock("lock:test","app1",30L);
        if(locked)
            return "Da lay duoc lock";
        return "Chua lay duoc lock";
    }




}
