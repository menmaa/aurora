/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.auth.exception;

public class InvalidTokenSignatureException extends AuroraAuthenticationException {
    public InvalidTokenSignatureException() {
        super("Invalid Auth Token Signature");
    }
}
