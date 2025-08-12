/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.auth;

public class AuroraSecurityContextImpl implements AuroraSecurityContext {

    private AuroraAuthentication authentication;

    public AuroraSecurityContextImpl(AuroraAuthentication authentication) {
        this.authentication = authentication;
    }

    @Override
    public AuroraAuthentication getAuthentication() {
        return authentication;
    }

    @Override
    public void setAuthentication(AuroraAuthentication authentication) {
        this.authentication = authentication;
    }
}
