package com.example.employee_manage_project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisLockService {
    private final StringRedisTemplate redisTemplate;

    public boolean tryLock(String key, String value, Long seconds)
    {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key,value, Duration.ofSeconds(seconds));
        return Boolean.TRUE.equals(success);
    }

    private static final String UNLOCK_SCRIPT = """
            if redis.call('get',KEYS[1]) == ARGV[1] then
                return redis.call('del',KEYS[1])
            else 
                return 0
            end""";
    public boolean unlock(String key, String value)
    {
        Long result = redisTemplate.execute(RedisScript.of(UNLOCK_SCRIPT,Long.class), List.of(key),value);
        return Long.valueOf(1).equals(result);
    }

}
