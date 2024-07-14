package com.blog.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class JwtBlackListService {
    @Value("${app.jwt-blackList-milliseconds}")
    private long jwtExpiration;

    private final RedisTemplate<String, String> redisTemplate;
    private static final String BLACKLIST_PREFIX = "jwt:blacklist:"; // 黑名單前綴

    /**
     * 將jwt加入黑名單
     *
     * @param jwtToken jwtToken
     *
     * */
    public void addJwtToBlackList(String jwtToken) {
        String key = BLACKLIST_PREFIX + jwtToken;
        Duration expirationDuration  = Duration.ofMillis(jwtExpiration); // 設置存活時間
        redisTemplate.opsForValue().set(key, "true", expirationDuration);
    }

    /**
     * 檢查jwt是否在黑名單中
     * @param jwtToken jwtToken
     * @return boolean 是否存在
     * */
    public boolean isJwtInBlackList(String jwtToken) {
        String key = BLACKLIST_PREFIX + jwtToken;
        return redisTemplate.opsForValue().get(key) != null;
    }
}