package com.project3.gatewayserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    public void saveUserSelectedCategory(String key, String value) {
        try {
            stringRedisTemplate.opsForValue().set(key, value);
        } catch (Exception error) {
            log.error("GatewayService Cache saveUserSelectedCategory() method error:", error);
        }
    }

    public List<String> saveLoggedInUserToCache(String key, String value) {
        try {
            Long index = stringRedisTemplate.opsForList().indexOf(key, value);
            if (index == null) {
                stringRedisTemplate.opsForList().leftPush(key, value);
                log.info("User {} added", value);
            }
            return getLoggedInUsersFromCache(key);
        } catch (Exception error) {
            log.error("GatewayService Cache saveLoggedInUserToCache() method error:", error);
            return new ArrayList<>();
        }
    }

    public List<String> getLoggedInUsersFromCache(String key) {
        try {
            List<String> response = stringRedisTemplate.opsForList().range(key, 0, -1);
            return response != null ? response : new ArrayList<>();
        } catch (Exception error) {
            log.error("GatewayService Cache getLoggedInUsersFromCache() method error:", error);
            return new ArrayList<>();
        }
    }

    public List<String> removeLoggedInUserFromCache(String key, String value) {
        try {
            stringRedisTemplate.opsForList().remove(key, 1, value);
            log.info("User {} removed", value);
            return getLoggedInUsersFromCache(key);
        } catch (Exception error) {
            log.error("GatewayService Cache removeLoggedInUserFromCache() method error:", error);
            return new ArrayList<>();
        }
    }
}
