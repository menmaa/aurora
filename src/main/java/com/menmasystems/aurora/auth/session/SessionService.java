/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.auth.session;

import com.menmasystems.aurora.auth.AuroraAuthentication;
import com.menmasystems.aurora.util.SnowflakeId;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class SessionService {

    private static final String REDIS_SESSION_KEY = "sessions:";
    private final ReactiveStringRedisTemplate redisTemplate;

    public SessionService(ReactiveStringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Mono<String> createSession(SnowflakeId userId) {
        int timestamp = Math.toIntExact(System.currentTimeMillis() / 1000);
        String key = REDIS_SESSION_KEY + userId;
        return redisTemplate.opsForSet().add(key, String.valueOf(timestamp))
                .map(result -> AuroraAuthentication.generateSignedToken(userId, timestamp));
    }

    public Mono<Boolean> isSessionValid(AuroraAuthentication authToken) {
        String key = REDIS_SESSION_KEY + authToken.userId();
        String value = String.valueOf(authToken.timestamp());
        return redisTemplate.opsForSet().isMember(key, value);
    }

    public Mono<AuroraAuthentication> getSession(String token) {
        return Mono.just(token)
                .flatMap(AuroraAuthentication::verifySignedToken)
                .filterWhen(this::isSessionValid);
    }

    public Mono<Void> invalidateSession(String userId, String timestamp) {
        String key = REDIS_SESSION_KEY + userId;
        return redisTemplate.opsForSet().remove(key, timestamp).then();
    }

    public Mono<Void> invalidateSession(AuroraAuthentication authToken) {
        String userId = String.valueOf(authToken.userId());
        String timestamp = String.valueOf(authToken.timestamp());
        return invalidateSession(userId, timestamp);
    }
}
