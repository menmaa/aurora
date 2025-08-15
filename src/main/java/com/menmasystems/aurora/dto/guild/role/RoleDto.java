/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.dto.guild.role;

import com.menmasystems.aurora.database.model.GuildRoleDocument;
import com.menmasystems.aurora.util.SnowflakeId;

public class RoleDto {

    private final SnowflakeId id;
    private final String name;
    private final long permissions;
    private final int color;
    private final boolean hoist;
    private final int position;
    private final boolean managed;
    private final boolean mentionable;

    public RoleDto(GuildRoleDocument role) {
        this.id = role.getId();
        this.name = role.getName();
        this.permissions = role.getPermissions();
        this.color = role.getColor();
        this.hoist = role.isHoist();
        this.position = role.getPosition();
        this.managed = role.isManaged();
        this.mentionable = role.isMentionable();
    }

    public SnowflakeId getId() {
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
