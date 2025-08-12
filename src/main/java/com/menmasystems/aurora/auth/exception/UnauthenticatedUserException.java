/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.auth.exception;

public class UnauthenticatedUserException extends AuroraAuthenticationException {
    public UnauthenticatedUserException() {
        super("User is not authenticated");
    }
}
