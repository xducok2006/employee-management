package com.example.employee_manage_project.config;

import com.example.employee_manage_project.dto.department.DepartmentResponseDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

@Configuration
public class RedisConfig {
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory,ObjectMapper objectMapper)
    {
        JacksonJsonRedisSerializer<DepartmentResponseDTO> serializer = new JacksonJsonRedisSerializer<>(objectMapper,DepartmentResponseDTO.class);
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(1)).serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                        serializer
                )
        );
        return RedisCacheManager.builder(connectionFactory).cacheDefaults(configuration).build();
    }
}
