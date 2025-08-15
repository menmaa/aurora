/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.database.model;

import com.menmasystems.aurora.dto.guild.role.CreateGuildRoleRequest;
import com.menmasystems.aurora.util.SnowflakeId;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode
@Document
public class GuildRoleDocument {

    private SnowflakeId id;
    private String name;
    private long permissions;
    private int color;
    private boolean hoist;
    private int position;
    private boolean managed;
    private boolean mentionable;

    public GuildRoleDocument() {}

    public GuildRoleDocument(SnowflakeId id, String name) {
        this.id = id;
        this.name = name;
    }

    public GuildRoleDocument(SnowflakeId id, CreateGuildRoleRequest request) {
        this.id = id;
        this.name = request.getName();
        this.permissions = request.getPermissions();
        this.color = request.getColor();
        this.hoist = request.isHoist();
        this.position = request.getPosition();
        this.mentionable = request.isMentionable();
    }

    public SnowflakeId getId() {
        return id;
    }

    public void setId(SnowflakeId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPermissions() {
        return permissions;
    }

    public void setPermissions(long permissions) {
        this.permissions = permissions;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isHoist() {
        return hoist;
    }

    public void setHoist(boolean hoist) {
        this.hoist = hoist;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isManaged() {
        return managed;
    }

    public void setManaged(boolean managed) {
        this.managed = managed;
    }

    public boolean isMentionable() {
        return mentionable;
    }

    public void setMentionable(boolean mentionable) {
        this.mentionable = mentionable;
    }
}
