/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.component;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
class RedisCuckooInitializer implements CommandLineRunner {
    private final RedisCuckooFilter redisFilter;

    public RedisCuckooInitializer(RedisCuckooFilter redisFilter) {
        this.redisFilter = redisFilter;
    }

    @Override
    public void run(String... args) {
        redisFilter.initializeFilters();
    }
}
