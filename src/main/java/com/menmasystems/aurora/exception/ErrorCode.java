/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.exception;

public enum ErrorCode {
    INVALID_REQUEST_BODY(1000, 400, "Invalid request body"),
    GUILD_NOT_FOUND(1001, 404, "Unknown Guild"),
    ROLE_NOT_FOUND(1002, 404, "Unknown Role"),
    USER_NOT_FOUND(1003, 404, "Unknown User"),
    CHANNEL_NOT_FOUND(1004, 404, "Unknown Channel"),
    MISSING_PERMISSIONS(2001, 403, "Missing Permissions"),
    MISSING_ACCESS(2002, 403, "Missing Access"),
    USER_AUTH_INVALID_CREDS(3001, 400,"Invalid Email/Password Combination"),
    USER_AUTH_REQUIRE_MFA(3002, 403, "Additional Verification Required"),
    CAPTCHA_FAILED(4001, 400, "Bad Request");

    private final int code;
    private final int statusCode;
    private final String message;

    ErrorCode(int code, int statusCode, String message) {
        this.code = code;
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
