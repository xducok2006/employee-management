package com.example.employee_manage_project.controller;

import com.example.employee_manage_project.dto.ApiResponse;
import com.example.employee_manage_project.dto.attendance.AttendanceResponseDTO;
import com.example.employee_manage_project.dto.attendance.LateAttendanceResponseDTO;
import com.example.employee_manage_project.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
@SecurityRequirement(name = "Token")
public class AttendanceController {
    private final AttendanceService attendanceService;
    @Operation(
            summary = "Employee check in",
            description = "Employee thực hiện check-in cho ngày làm việc hiện tại."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Check in thành công"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Chưa đăng nhập"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Employee đã check in trong ngày"
            )
    })
    @PostMapping("/check-in")
    public ResponseEntity<ApiResponse<AttendanceResponseDTO>> checkIn() throws InterruptedException {
        ApiResponse<AttendanceResponseDTO> response = new ApiResponse<>(201,"Call api successfully",attendanceService.checkIn());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @Operation(
            summary = "Employee check out",
            description = "Check out cho ngày làm việc hiện tại."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Check out thành công"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Chưa đăng nhập"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Chưa check in ngày hôm nay"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Đã check out ngày hôm nay"
            )
    })
    @PutMapping("/check-out")
    public ResponseEntity<ApiResponse<AttendanceResponseDTO>> checkOut()
    {
        ApiResponse<AttendanceResponseDTO> response = new ApiResponse<>(200,"Call api successfully",attendanceService.checkOut());
        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "Xem lịch sử chấm công của tất cả nhân viên"
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
    public ResponseEntity<ApiResponse<List<AttendanceResponseDTO>>> getAll()
    {
        ApiResponse<List<AttendanceResponseDTO>> response = new ApiResponse<>(200,"Call api successfully",attendanceService.getAll());
        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "Xem lịch sử chấm công của nhân viên"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Lấy lịch sử chấm công thành công"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Chưa đăng nhập"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Không đủ quyền xem lịch sử chấm công của nhân viên này"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy nhân viên này"
            )
    })
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<List<AttendanceResponseDTO>>> getAllByEmployee(@PathVariable Long employeeId)
    {
        ApiResponse<List<AttendanceResponseDTO>> response = new ApiResponse<>(200,"Call api successfully",attendanceService.getByEmployeeId(employeeId));
        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "Lấy lịch sử chấm công theo tháng của tất cả nhân viên"
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
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Tháng hoặc năm không hợp lệ"
            )
    })
    @GetMapping("/month")
    public ResponseEntity<ApiResponse<List<AttendanceResponseDTO>>> getAllByMonth(@RequestParam int month, @RequestParam int year)
    {
        ApiResponse<List<AttendanceResponseDTO>> response = new ApiResponse<>(200,"Call api successfully",attendanceService.getAllByMonth(month,year));
        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "Lấy lịch sử chấm công theo tháng của tất cả nhân viên"
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
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Tháng hoặc năm không hợp lệ"
            )
    })
    @GetMapping("/month/late")
    public ResponseEntity<ApiResponse<List<AttendanceResponseDTO>>> getAllByLateInMonth(@RequestParam int month, @RequestParam int year)
    {
        ApiResponse<List<AttendanceResponseDTO>> response = new ApiResponse<>(200,"Call api successfully",attendanceService.getAllByLateInMonth(month,year));
        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "Lấy lịch sử chấm công theo tháng của tất cả nhân viên"
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
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Tháng hoặc năm không hợp lệ"
            )
    })
    @GetMapping("month/on-time")
    public ResponseEntity<ApiResponse<List<AttendanceResponseDTO>>> getAllByOnTimeInMonth(@RequestParam int month, @RequestParam int year)
    {
        ApiResponse<List<AttendanceResponseDTO>> response = new ApiResponse<>(200,"Call api successfully",attendanceService.getAllByOnTimeInMonth(month,year));
        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "Lấy lịch sử chấm công theo tháng của tất cả nhân viên"
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
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Tháng hoặc năm không hợp lệ"
            )
    })
    @GetMapping("/month/most-late")
    public ResponseEntity<ApiResponse<List<LateAttendanceResponseDTO>>> getAllByMostLateInMonth(@RequestParam int month, @RequestParam int year)
    {
        ApiResponse<List<LateAttendanceResponseDTO>> response = new ApiResponse<>(200,"Call api successfully",attendanceService.getAllByMostLateInMonth(month,year));
        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "Lấy file excel chấm công"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Xuất file thành công"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Chưa đăng nhập"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Không có đủ quyền"
            )
    })
    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel() throws IOException {
        byte[] data = attendanceService.exportAttendanceExcel();
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment;filename=attendance.xlsx").
                contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")).body(data);
    }
    @Operation(
            summary = "Xem lịch sử chấm công của bản thân"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Lấy lịch sử chấm công thành công."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Chưa đăng nhập"
            )
    })
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<AttendanceResponseDTO>>> getAllMySelf()
    {
        ApiResponse<List<AttendanceResponseDTO>> response = new ApiResponse<>(200,"Call api successfully",attendanceService.getAllMySelf());
        return ResponseEntity.ok(response);
    }
}
