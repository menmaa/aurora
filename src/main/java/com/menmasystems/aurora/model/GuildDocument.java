/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.model;

import com.menmasystems.aurora.util.SnowflakeId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "guilds")
public class GuildDocument {

    @Id
    private long id;
    private String name;
    private String icon;
    private SnowflakeId ownerId;
    private List<RoleDocument> roles;

    public SnowflakeId getId() {
        return SnowflakeId.of(id);
    }

    public void setId(SnowflakeId id) {
        this.id = id.id();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public SnowflakeId getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(SnowflakeId ownerId) {
        this.ownerId = ownerId;
    }

    public List<RoleDocument> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDocument> roles) {
        this.roles = roles;
    }
}
