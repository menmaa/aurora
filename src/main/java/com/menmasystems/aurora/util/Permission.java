/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.util;

import java.util.Arrays;

public enum Permission {
    NONE(0),
    ADMINISTRATOR,
    MANAGE_GUILD,
    MANAGE_ROLES,
    MANAGE_CHANNELS,
    MANAGE_NICKNAMES;

    private final long value;

    Permission() {
        this.value = 1L << (ordinal() - 1);
    }

    Permission(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    public static long getPermissionMask(Permission... permissions) {
        return Arrays.stream(permissions)
                .map(Permission::getValue)
                .reduce(0L, (a, b) -> a | b);
    }

    public static Permission[] getPermissions(long permissions) {
        return Arrays.stream(Permission.values())
                .filter(p -> (permissions & p.getValue()) != 0)
                .toArray(Permission[]::new);
    }
}
