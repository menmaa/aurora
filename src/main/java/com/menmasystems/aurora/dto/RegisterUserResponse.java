/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.dto;

import com.menmasystems.aurora.util.SnowflakeId;

public class RegisterUserResponse {
    private final SnowflakeId id;

    public RegisterUserResponse(SnowflakeId id) {
        this.id = id;
    }

    public SnowflakeId getId() {
        return id;
    }
}
