/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.model;

import com.menmasystems.aurora.util.SnowflakeId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class RoleDocument {

    private SnowflakeId id;
    private String name;
    private long permissions;
    private int color;
    private boolean hoist;
    private int position;
    private boolean managed;
    private boolean mentionable;

    public RoleDocument() {}

    public RoleDocument(SnowflakeId id, String name) {
        this.id = id;
        this.name = name;
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
