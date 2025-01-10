package com.project3.gigs.redis;

import io.lettuce.core.RedisConnectionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class GigCacheService {
    private final RedisTemplate<String, String> redisTemplate;

    public String getUserSelectedCategory(String key) {
        try {
            if (!isRedisConnected()) {
                connectRedis();
            }
            String value = redisTemplate.opsForValue().get(key);
            log.debug("Retrieved value for key {}: {}", key, value);
            return value;
        } catch (Exception error) {
            log.error("Error getting value for key {}: {}", key, error.getMessage(), error);
            return "";
        }
    }

    private boolean isRedisConnected() {
        try {
            return Boolean.TRUE.equals(
                    Objects.requireNonNull(redisTemplate.getConnectionFactory())
                            .getConnection()
                            .ping()
            );
        } catch (Exception e) {
            log.warn("Redis connection check failed: {}", e.getMessage());
            return false;
        }
    }

    private void connectRedis() {
        try {
            RedisConnection connection = Objects.requireNonNull(redisTemplate.getConnectionFactory())
                    .getConnection();
            log.info("Successfully connected to Redis");
            connection.close();
        } catch (Exception error) {
            log.error("Failed to connect to Redis: {}", error.getMessage(), error);
            throw new RedisConnectionException("Could not connect to Redis", error);
        }
    }
}
