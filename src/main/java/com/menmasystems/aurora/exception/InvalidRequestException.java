/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.exception;

public class InvalidRequestException extends ApiException {
    public InvalidRequestException() {
        super(ErrorCode.INVALID_REQUEST_BODY);
    }
}
