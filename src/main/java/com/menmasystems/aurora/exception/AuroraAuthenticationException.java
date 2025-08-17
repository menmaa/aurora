/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.exception;

public abstract class AuroraAuthenticationException extends Exception {
    public AuroraAuthenticationException(String message) {
        super(message);
    }
}
