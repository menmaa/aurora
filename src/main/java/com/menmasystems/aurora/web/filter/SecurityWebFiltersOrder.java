/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.web.filter;

public enum SecurityWebFiltersOrder {
    CORS(0),
    AUTHENTICATION,
    AUTHORIZATION;

    private final int order;

    SecurityWebFiltersOrder(int order) {
        this.order = order;
    }

    SecurityWebFiltersOrder() {
        this.order = ordinal();
    }

    public int getOrder() {
        return this.order;
    }
}
