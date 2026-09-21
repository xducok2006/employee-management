package com.example.employee_manage_project.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedissonLockService {

    private final RedissonClient redissonClient;

    public boolean tryLockWithWatchDog(String key, long waitTime) throws InterruptedException {
        RLock lock = redissonClient.getLock(key);

        return lock.tryLock(waitTime,TimeUnit.SECONDS);
    }

    public boolean tryLock(String key, long waitTime, long leaseTime) throws InterruptedException {
        RLock lock = redissonClient.getLock(key);

        return lock.tryLock(waitTime, leaseTime,TimeUnit.SECONDS);
    }

    public void unlock(String key)
    {
        RLock lock = redissonClient.getLock(key);

        if(lock.isHeldByCurrentThread())
        {
            lock.unlock();
        }
    }
}
