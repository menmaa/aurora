/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.exception;

public class InvalidTokenSignatureException extends RuntimeException {
    public InvalidTokenSignatureException() {
        super("Invalid Auth Token Signature");
    }
}
