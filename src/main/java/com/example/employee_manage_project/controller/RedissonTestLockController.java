package com.example.employee_manage_project.controller;

import com.example.employee_manage_project.service.RedissonLockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test-redisson")
@RequiredArgsConstructor
public class RedissonTestLockController {
    private final RedissonLockService redissonLockService;

    @GetMapping
    public String testLock() throws InterruptedException {
        String key = "lock:test:attendance";

        boolean locked = redissonLockService.tryLock(key,5,30);

        if(!locked)
            throw new RuntimeException("Không lấy được lock");

        try {
            Thread.sleep(40000);
            return "Đã xử lý xong";
        }
        finally {
            redissonLockService.unlock(key);
        }

    }
    @GetMapping("/watchdog")
    public String testLockWithWatchDog() throws InterruptedException {
        String key = "lock:test:watchdog";

        boolean locked = redissonLockService.tryLockWithWatchDog(key,5);

        if(!locked)
            throw new RuntimeException("Không lây được lock");

        try {
            Thread.sleep(40000);
            return "Đã xử lý xong";
        }
        finally {
            redissonLockService.unlock(key);
        }

    }
}
