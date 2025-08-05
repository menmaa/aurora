/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.component;

import com.menmasystems.aurora.redis.RedisBloomCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisCuckooFilter {
    private static final String USERNAME_FILTER_KEY = "cf:usernames";
    private static final String EMAIL_FILTER_KEY = "cf:emails";

    private final StringRedisTemplate redisTemplate;
    private final RedisBloomCommands redisBloom;

    public RedisCuckooFilter(StringRedisTemplate redisTemplate, RedisBloomCommands redisBloom) {
        this.redisTemplate = redisTemplate;
        this.redisBloom = redisBloom;
    }

    public void initializeFilters() {
        if(!redisTemplate.hasKey(USERNAME_FILTER_KEY)) {
            redisBloom.cfReserve(USERNAME_FILTER_KEY, 1000);
        }

        if(!redisTemplate.hasKey(EMAIL_FILTER_KEY)) {
            redisBloom.cfReserve(EMAIL_FILTER_KEY, 1000);
        }
    }

    public void addUsername(String username) {
        redisBloom.cfAdd(USERNAME_FILTER_KEY, username.toLowerCase());
    }

    public void addEmail(String email) {
        redisBloom.cfAdd(EMAIL_FILTER_KEY, email.toLowerCase());
    }

    public void delUsername(String username) {
        redisBloom.cfDel(USERNAME_FILTER_KEY, username.toLowerCase());
    }

    public void delEmail(String email) {
        redisBloom.cfDel(EMAIL_FILTER_KEY, email.toLowerCase());
    }

    public boolean maybeUsernameExists(String username) {
        return redisBloom.cfExists(USERNAME_FILTER_KEY, username.toLowerCase());
    }

    public boolean maybeEmailExists(String email) {
        return redisBloom.cfExists(EMAIL_FILTER_KEY, email.toLowerCase());
    }
}
