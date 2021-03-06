package com.example.api.service;

import com.example.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class RefreshTokenService {

    private final RedisTemplate redisTemplate;
    private final long REFRESH_TOKEN_EXPIRE_TIME = TokenProvider.REFRESH_TOKEN_EXPIRE_TIME;

    @Transactional
    public void setValue(String accountId, String token) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(accountId, token, Duration.ofMillis(REFRESH_TOKEN_EXPIRE_TIME));
    }

    public String getValue(String accountId) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return values.get(accountId);
    }

    public Boolean hasKey(String accountId) {
        return redisTemplate.hasKey(accountId);
    }

    @Transactional
    public void delValue(String accountId) {
        redisTemplate.delete(accountId);
    }

}
