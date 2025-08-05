/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.service;

import com.menmasystems.aurora.auth.AuroraAuthenticationToken;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class SessionService {

    private static final String REDIS_SESSION_KEY = "sessions:";
    private final StringRedisTemplate redisTemplate;

    public SessionService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Mono<String> createSession(String userId) {
        int timestamp = Math.toIntExact(System.currentTimeMillis() / 1000);
        String key = REDIS_SESSION_KEY + userId;
        redisTemplate.opsForSet().add(key, String.valueOf(timestamp));

        return Mono.just(AuroraAuthenticationToken.generateSignedToken(userId, timestamp));
    }

    public Mono<Boolean> isSessionValid(AuroraAuthenticationToken authToken) {
        String key = REDIS_SESSION_KEY + authToken.getPrincipal();
        String value = String.valueOf(authToken.getCredentials());
        return Mono.just(Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value)));
    }

    public void invalidateSession(String principalId, String timestamp) {
        String key = REDIS_SESSION_KEY + principalId;
        redisTemplate.opsForSet().remove(key, timestamp);
    }

    public Mono<Void> invalidateSession(AuroraAuthenticationToken authToken) {
        invalidateSession(authToken.getPrincipal(), String.valueOf(authToken.getCredentials()));
        return Mono.empty();
    }
}
