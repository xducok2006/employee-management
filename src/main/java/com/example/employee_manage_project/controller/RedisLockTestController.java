package com.example.employee_manage_project.controller;

import com.example.employee_manage_project.service.RedisLockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/redistest")
public class RedisLockTestController {
    private final RedisLockService redisLockService;

    @GetMapping
    public String testLock() throws InterruptedException {
        String key = "lock:test";
        String uuid = UUID.randomUUID().toString();
        if(!redisLockService.tryLock(key,uuid,30L))
            throw new RuntimeException("Không lấy được lock");

        try {
            Thread.sleep(5000);
            return "Đã xử lý xong";
        } finally {
            redisLockService.unlock(key,uuid);
        }
    }
}
