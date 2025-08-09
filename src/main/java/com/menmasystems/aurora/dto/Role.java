/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.dto;

import com.menmasystems.aurora.model.RoleDocument;

public class Role {

    private final String id;
    private final String name;
    private final long permissions;
    private final int color;
    private final boolean hoist;
    private final int position;
    private final boolean managed;
    private final boolean mentionable;

    public Role(RoleDocument role) {
        this.id = role.getId();
        this.name = role.getName();
        this.permissions = role.getPermissions();
        this.color = role.getColor();
        this.hoist = role.isHoist();
        this.position = role.getPosition();
        this.managed = role.isManaged();
        this.mentionable = role.isMentionable();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getPermissions() {
        return permissions;
    }

    public int getColor() {
        return color;
    }

    public boolean isHoist() {
        return hoist;
    }

    public int getPosition() {
        return position;
    }

    public boolean isManaged() {
        return managed;
    }

    public boolean isMentionable() {
        return mentionable;
    }
}
