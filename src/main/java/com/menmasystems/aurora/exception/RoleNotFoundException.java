/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.exception;

public class RoleNotFoundException extends ApiException {
    public RoleNotFoundException(long roleId) {
        super(ErrorCode.ROLE_NOT_FOUND);
    }
}
