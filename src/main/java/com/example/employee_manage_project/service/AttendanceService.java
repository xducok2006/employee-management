package com.example.employee_manage_project.service;

import com.example.employee_manage_project.dto.attendance.AttendanceResponseDTO;
import com.example.employee_manage_project.dto.attendance.LateAttendanceResponseDTO;
import com.example.employee_manage_project.entity.Attendance;
import com.example.employee_manage_project.entity.Employee;
import com.example.employee_manage_project.exception.HandleAlreadyExists;
import com.example.employee_manage_project.exception.HandleNotFound;
import com.example.employee_manage_project.repository.AttendanceRepository;
import com.example.employee_manage_project.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final JwtService jwtService;
    private final RedissonLockService redissonLockService;

    @Value("${server.port}")
    private String port;
    @PreAuthorize("hasAuthority('ATTENDANCE_CHECK_IN')")
    @Transactional

    public AttendanceResponseDTO checkIn() throws InterruptedException {
        String username = jwtService.getCurrentUser();
        Employee employee = employeeRepository.findEmployeeByUsername(username).orElseThrow(()->new HandleNotFound("Nhân viên không tồn tại"));
        LocalDate today = LocalDate.now();

        String lockKey = "lock:attendance:"+employee.getId()+":"+today;

        boolean locked = redissonLockService.tryLockWithWatchDog(lockKey,5);

        if(!locked)
            throw new RuntimeException("Attendance đang được xử lý");

        if(attendanceRepository.existsByEmployee_UsernameAndWorkDate(username, today))
            throw new HandleAlreadyExists("Nhân viên đã check in hôm nay");

        try {

            LocalDateTime checkIn = LocalDateTime.now();
            LocalTime checkInTime = checkIn.toLocalTime();
            LocalTime standardTime = LocalTime.of(8,0);
            Attendance attendance = Attendance.builder().workDate(today).checkIn(checkIn).employee(employee).build();
            if(checkInTime.isAfter(standardTime))
            {
                attendance.setStatus(Attendance.Status.LATE);
                attendance.setLateMinutes(Duration.between(standardTime,checkInTime).toMinutes());
            }
            else
            {
                attendance.setStatus(Attendance.Status.ON_TIME);
                attendance.setLateMinutes(0L);
            }
            Attendance savedAttendance = attendanceRepository.save(attendance);
            return AttendanceResponseDTO.builder().id(savedAttendance.getId()).
                    employeeId(savedAttendance.getEmployee().getId()).employeeName(savedAttendance.getEmployee().getFullName())
                    .workDate(savedAttendance.getWorkDate()).checkIn(savedAttendance.getCheckIn()).
                    checkOut(savedAttendance.getCheckOut()).status(savedAttendance.getStatus()).lateMinutes(savedAttendance.getLateMinutes()).version(savedAttendance.getVersion()).build();
        }
        finally {
            redissonLockService.unlock(lockKey);
            log.info("UNLOCK - port={}",
                    ((ServletRequestAttributes) RequestContextHolder
                            .currentRequestAttributes())
                            .getRequest()
                            .getLocalPort());
        }

    }
    @PreAuthorize("hasAuthority('ATTENDANCE_CHECK_OUT')")
    @Transactional
    public AttendanceResponseDTO checkOut() throws InterruptedException {
        String username =jwtService.getCurrentUser();
        LocalDate today = LocalDate.now();
        Employee employee = employeeRepository.findEmployeeByUsername(username).orElseThrow(()->new HandleNotFound("Nhân viên không tồn tại"));
        String lockKey = "lock:attendance:"+employee.getId()+":"+today;

        boolean locked = redissonLockService.tryLockWithWatchDog(lockKey,5);

        if(!locked)
            throw new RuntimeException("Attendance đang được xử lý");

        Attendance attendance = attendanceRepository.findByEmployee_UsernameAndWorkDate(username, today).
                orElseThrow(()->new HandleNotFound("Nhân viên chưa check in hôm nay "));
        if(attendance.getCheckOut()!=null)
            throw new HandleAlreadyExists("Nhân viên đã check out ngày hôm nay");

        try {
            LocalDateTime checkOutTime = LocalDateTime.now();
            attendance.setCheckOut(checkOutTime);
            Attendance savedAttendance = attendanceRepository.save(attendance);
            return AttendanceResponseDTO.builder().id(savedAttendance.getId()).
                    employeeId(savedAttendance.getEmployee().getId()).employeeName(savedAttendance.getEmployee().getFullName())
                    .workDate(savedAttendance.getWorkDate()).checkIn(savedAttendance.getCheckIn()).
                    checkOut(savedAttendance.getCheckOut()).status(savedAttendance.getStatus()).lateMinutes(savedAttendance.getLateMinutes()).version(savedAttendance.getVersion()).build();
        }
        finally {
            redissonLockService.unlock(lockKey);
        }

    }
    @PreAuthorize("hasAuthority('ATTENDANCE_READ_ALL')")
    public List<AttendanceResponseDTO> getAll()
    {
        String username = jwtService.getCurrentUser();
        Employee employee = employeeRepository.findEmployeeByUsername(username).orElseThrow();
        return attendanceRepository.findAllByEmployee_Department(employee.getDepartment()).stream().map(
                attendance ->
                    AttendanceResponseDTO.builder()
                        .id(attendance.getId()).employeeId(attendance.getEmployee().getId()).
                        employeeName(attendance.getEmployee().getFullName()).workDate(attendance.getWorkDate()).
                        checkIn(attendance.getCheckIn()).checkOut(attendance.getCheckOut()).status(attendance.getStatus()).lateMinutes(attendance.getLateMinutes()).version(attendance.getVersion()).build()

        ).toList();
    }
    @PreAuthorize("hasAuthority('ATTENDANCE_READ_ALL')")
    public List<AttendanceResponseDTO> getByEmployeeId(Long employeeId)
    {
        return attendanceRepository.findAllByEmployee_Id(employeeId).stream().
                map(attendance -> AttendanceResponseDTO.builder()
                        .id(attendance.getId()).employeeId(attendance.getEmployee().getId()).employeeName(attendance.getEmployee().getFullName()).
                        workDate(attendance.getWorkDate()).checkIn(attendance.getCheckIn()).checkOut(attendance.getCheckOut()).
                        status(attendance.getStatus()).lateMinutes(attendance.getLateMinutes()).build()).toList();
    }
    @PreAuthorize("hasAuthority('ATTENDANCE_READ_ALL')")
    public List<AttendanceResponseDTO> getAllByMonth(int month, int year)
    {
        String username = jwtService.getCurrentUser();
        Employee employee = employeeRepository.findEmployeeByUsername(username).orElseThrow();
        LocalDate startDate = LocalDate.of(year,month,1);
        LocalDate endDate = startDate.plusMonths(1);
        return attendanceRepository.findAllByEmployee_DepartmentAndInWorkDate(startDate,endDate,employee.getDepartment()).stream().map(
                attendance -> AttendanceResponseDTO.builder()
                        .id(attendance.getId()).employeeId(attendance.getEmployee().getId()).
                        employeeName(attendance.getEmployee().getFullName()).workDate(attendance.getWorkDate()).
                        checkIn(attendance.getCheckIn()).checkOut(attendance.getCheckOut()).status(attendance.getStatus()).lateMinutes(attendance.getLateMinutes()).build()
        ).toList();
    }
    @PreAuthorize("hasAuthority('ATTENDANCE_READ_ALL')")
    public List<AttendanceResponseDTO> getAllByLateInMonth(int month, int year)
    {
        LocalDate startDate = LocalDate.of(year,month,1);
        LocalDate endDate = startDate.plusMonths(1);
        return attendanceRepository.findAllByStatusInTime(startDate,endDate,Attendance.Status.LATE).stream()
                .map(attendance -> AttendanceResponseDTO.builder().
                        id(attendance.getId()).employeeId(attendance.getEmployee().getId()).employeeName(attendance.getEmployee().getFullName()).
                        workDate(attendance.getWorkDate()).checkIn(attendance.getCheckIn()).checkOut(attendance.getCheckOut()).
                        status(attendance.getStatus()).lateMinutes(attendance.getLateMinutes()).build()).toList();
    }
    @PreAuthorize("hasAuthority('ATTENDANCE_READ_ALL')")
    public List<AttendanceResponseDTO> getAllByOnTimeInMonth(int month, int year)
    {

        LocalDate startDate = LocalDate.of(year,month,1);
        LocalDate endDate = startDate.plusMonths(1);
        return attendanceRepository.findAllByStatusInTime(startDate,endDate,Attendance.Status.ON_TIME).stream()
                .map(attendance -> AttendanceResponseDTO.builder().
                        id(attendance.getId()).employeeId(attendance.getEmployee().getId()).employeeName(attendance.getEmployee().getFullName()).
                        workDate(attendance.getWorkDate()).checkIn(attendance.getCheckIn()).checkOut(attendance.getCheckOut()).
                        status(attendance.getStatus()).lateMinutes(attendance.getLateMinutes()).build()).toList();
    }
    @PreAuthorize("hasAuthority('ATTENDANCE_READ_ALL')")
    public List<LateAttendanceResponseDTO> getAllByMostLateInMonth(int month, int year)
    {
        LocalDate startDate = LocalDate.of(year,month,1);
        LocalDate endDate = startDate.plusMonths(1);
        return attendanceRepository.findByEmployeeWithMostLateInMonth(startDate,endDate).stream().map(reuslt ->
                LateAttendanceResponseDTO.builder().employeeId((Long)reuslt[0]).employeeName((String) reuslt[1]).lateCount((Long) reuslt[2]).build()).toList();
    }
    public List<AttendanceResponseDTO> getAllMySelf()
    {
        String username = jwtService.getCurrentUser();
        Employee employee = employeeRepository.findEmployeeByUsername(username).orElseThrow(()->new HandleNotFound("Không tìm thấy nhân viên này"));
        return attendanceRepository.findAllByEmployee_Id(employee.getId()).stream().map(result ->
                AttendanceResponseDTO.builder().id(result.getId()).
                        employeeId(result.getEmployee().getId()).employeeName(result.getEmployee().getFullName()).workDate(result.getWorkDate()).checkIn(result.getCheckIn()).checkOut(result.getCheckOut()).status(result.getStatus()).lateMinutes(result.getLateMinutes()).build()).toList();
    }
    public String createMonthlyAttendanceReport(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = attendanceRepository.findAllByStatusInMonth(startDate, endDate, Attendance.Status.LATE);
        StringBuilder message = new StringBuilder();
        message.append(" BÁO CÁO CHẤM CÔNG\n");
        message.append("Từ ").append(startDate).append(" đến ").append(endDate).append("\n\n");
        message.append(" NHÂN VIÊN ĐI MUỘN\n");

        for (Object[] result : results) {

            Long employeeId = ((Number) result[0]).longValue();
            String employeeName = (String) result[1];
            Long lateCount = ((Number) result[2]).longValue();

            message.append(" ").append(employeeId).append(" - ").append(employeeName).append(" - ").append(lateCount).append(" lần\n");
        }
        message.append("\n ĐI MUỘN NHIỀU NHẤT\n");
        List<Object[]> resultWithMostLate = attendanceRepository.findByEmployeeWithMostLateInMonth(startDate, endDate);
        for (Object[] result : resultWithMostLate) {
            Long employeeId = ((Number) result[0]).longValue();
            String employeeName = (String) result[1];
            Long lateCount = ((Number) result[2]).longValue();

            message.append(" ").append(employeeId).append(" - ").append(employeeName).append(" - ").append(lateCount).append(" lần\n");
        }
        return message.toString();
    }
    public byte[] exportAttendanceExcel() throws IOException {
        List<Attendance> attendances = attendanceRepository.findAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Attendance");

        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("dd/MM/yyyy"));

        CellStyle dateTimeStyle = workbook.createCellStyle();
        dateTimeStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("dd/MM/yyyy HH:mm:ss"));

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("STT");
        header.createCell(1).setCellValue("Employee");
        header.createCell(2).setCellValue("Work Date");
        header.createCell(3).setCellValue("Check In");
        header.createCell(4).setCellValue("Check Out");
        header.createCell(5).setCellValue("Status");
        int rowIndex = 1;
        for (Attendance attendance : attendances)
        {
            Row row = sheet.createRow(rowIndex);
            row.createCell(0).setCellValue(rowIndex);
            row.createCell(1).setCellValue(attendance.getEmployee().getFullName());
            Cell workDateCell = row.createCell(2);
            if (attendance.getWorkDate() != null) {
                workDateCell.setCellValue(java.sql.Date.valueOf(attendance.getWorkDate()));
                workDateCell.setCellStyle(dateStyle);
            }
            Cell checkInCell = row.createCell(3);

            if (attendance.getCheckIn() != null) {
                checkInCell.setCellValue(java.sql.Timestamp.valueOf(attendance.getCheckIn()));
                checkInCell.setCellStyle(dateTimeStyle);
            }

            Cell checkOutCell = row.createCell(4);

            if (attendance.getCheckOut() != null) {
                checkOutCell.setCellValue(
                        java.sql.Timestamp.valueOf(attendance.getCheckOut())
                );
                checkOutCell.setCellStyle(dateTimeStyle);
            }
            row.createCell(5).setCellValue(attendance.getStatus().name());
            rowIndex++;
        }

        for(int i = 0;i<8;i++)
        {
            sheet.autoSizeColumn(i);
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }

    public void concurrentCheckOut(Long attendanceId, CountDownLatch ready, CountDownLatch go)
    {
        Attendance attendance = attendanceRepository.findById(attendanceId).orElseThrow();
        ready.countDown();
        try {
            go.await();
        }
        catch (InterruptedException ex)
        {
            Thread.currentThread().interrupt();
            throw new RuntimeException();
        }
        attendance.setCheckOut(LocalDateTime.now());
        attendanceRepository.saveAndFlush(attendance);
    }
}
