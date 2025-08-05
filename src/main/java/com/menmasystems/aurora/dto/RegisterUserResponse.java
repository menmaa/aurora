/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.dto;

public class RegisterUserResponse {
    private final String id;

    public RegisterUserResponse(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
