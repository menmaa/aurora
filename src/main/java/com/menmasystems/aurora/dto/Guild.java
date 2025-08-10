/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.dto;

import com.menmasystems.aurora.model.GuildDocument;
import com.menmasystems.aurora.util.SnowflakeId;

import java.util.List;

public class Guild {

    private final SnowflakeId id;
    private final String name;
    private final String icon;
    private final SnowflakeId ownerId;
    private final List<Role> roles;

    public Guild(GuildDocument guild) {
        this.id = guild.getId();
        this.name = guild.getName();
        this.icon = guild.getIcon();
        this.ownerId = guild.getOwnerId();
        this.roles = guild.getRoles().stream().map(Role::new).toList();
    }

    public SnowflakeId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public SnowflakeId getOwnerId() {
        return ownerId;
    }

    public List<Role> getRoles() {
        return roles;
    }
}
