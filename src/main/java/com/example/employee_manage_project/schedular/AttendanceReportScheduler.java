package com.example.employee_manage_project.schedular;

import com.example.employee_manage_project.service.AttendanceService;
import com.example.employee_manage_project.telegram.service.TelegramService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class AttendanceReportScheduler {

    private final AttendanceService attendanceService;
    private final TelegramService telegramService;

    @Scheduled(cron = "0 0 19 * * *")
    public void sendMonthlyAttendanceReport() {
        LocalDate now = LocalDate.now();
        if (now.getDayOfMonth() != now.lengthOfMonth()) {
            return;
        }
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = startDate.plusMonths(1);
        String message = attendanceService.createMonthlyAttendanceReport(startDate, endDate);
        telegramService.sendMessage(message);
    }

}