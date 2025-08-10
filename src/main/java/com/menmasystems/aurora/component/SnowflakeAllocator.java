/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.component;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class SnowflakeAllocator implements DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(SnowflakeAllocator.class);

    private static final String SNOWFLAKE_BITMAP_KEY = "snowflake:workers";
    private static final int SNOWFLAKE_BITMAP_SIZE = 31;

    private final StringRedisTemplate redisTemplate;
    private final AtomicInteger workerId = new AtomicInteger(-1);

    public SnowflakeAllocator(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void allocateWorkerId() {
        for(int id = 0; id < SNOWFLAKE_BITMAP_SIZE; id++) {
            Boolean wasSet = redisTemplate.opsForValue().setBit(SNOWFLAKE_BITMAP_KEY, id, true);
            if(Boolean.FALSE.equals(wasSet)) {
                workerId.set(id);
                log.info("Allocated Snowflake Worker ID: {}", workerId.get());
                return;
            }
        }
        throw new IllegalStateException("No available Snowflake Worker IDs");
    }

    public int getWorkerId() {
        return workerId.get();
    }

    @Override
    public void destroy() {
        if(workerId.get() != -1) {
            redisTemplate.opsForValue().setBit(SNOWFLAKE_BITMAP_KEY, workerId.get(), false);
            log.info("Released Snowflake Worker ID: {}", workerId.get());
        }
    }
}
