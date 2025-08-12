/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.auth;

import java.io.Serializable;

public interface AuroraSecurityContext extends Serializable {

    AuroraAuthentication getAuthentication();

    void setAuthentication(AuroraAuthentication authentication);
}
