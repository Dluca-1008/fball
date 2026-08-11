package com.football.community.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class TokenSessionService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String TOKEN_PREFIX = "session:token:";
    private static final long DEFAULT_TTL = 1800;

    public void saveToken(String token, Long userId) {
        redisTemplate.opsForValue().set(
                TOKEN_PREFIX + token,
                userId,
                DEFAULT_TTL,
                TimeUnit.SECONDS
        );
    }

    public Long validateToken(String token) {
        Object value = redisTemplate.opsForValue().getAndExpire(TOKEN_PREFIX + token, Duration.ofSeconds(1800));
        if (value instanceof Integer) return ((Integer) value).longValue();
        if (value instanceof Long) return (Long) value;
        return null;
    }

    public void removeToken(String token) {
        redisTemplate.delete(TOKEN_PREFIX + token);
    }

    /**
     * 吊销指定用户的所有 token（修改密码/封禁时使用）。
     * 通过 Redis SCAN 匹配 session:token:*，逐条检查 userId 并删除。
     */
    public void revokeUserTokens(Long userId) {
        ScanOptions options = ScanOptions.scanOptions()
                .match(TOKEN_PREFIX + "*")
                .count(100)
                .build();
        Cursor<String> cursor = redisTemplate.scan(options);
        while (cursor.hasNext()) {
            String key = cursor.next();
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null && userId.equals(toLong(value))) {
                redisTemplate.delete(key);
            }
        }
    }

    private static Long toLong(Object value) {
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        return null;
    }
}
