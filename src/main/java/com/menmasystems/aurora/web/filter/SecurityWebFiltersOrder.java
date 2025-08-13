/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.web.filter;

public enum SecurityWebFiltersOrder {
    CORS,
    AUTHENTICATION,
    AUTHORIZATION;

    public int getOrder() {
        return this.ordinal();
    }
}
