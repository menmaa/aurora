/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.dto.guild;

import com.menmasystems.aurora.dto.guild.role.RoleDto;
import com.menmasystems.aurora.database.model.GuildDocument;
import com.menmasystems.aurora.util.SnowflakeId;

import java.util.List;

public class GuildDto {

    private final SnowflakeId id;
    private final String name;
    private final String icon;
    private final SnowflakeId ownerId;
    private final List<RoleDto> roles;

    public GuildDto(GuildDocument guild) {
        this.id = guild.getId();
        this.name = guild.getName();
        this.icon = guild.getIcon();
        this.ownerId = guild.getOwnerId();
        this.roles = guild.getRoles().stream().map(RoleDto::new).toList();
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

    public List<RoleDto> getRoles() {
        return roles;
    }
}
