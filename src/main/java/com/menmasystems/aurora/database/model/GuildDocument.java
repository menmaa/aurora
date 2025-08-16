/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.database.model;

import com.menmasystems.aurora.util.SnowflakeId;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * Database document model for storing guild information.
 *
 * @author Fotis Makris (Menma)
 */
@EqualsAndHashCode
@Document(collection = "guilds")
public class GuildDocument {

    @Id
    private long id;
    private String name;
    private String icon;
    private SnowflakeId ownerId;
    private List<GuildRoleDocument> roles;
    private Long dateDeleted = null;

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

    public List<GuildRoleDocument> getRoles() {
        return roles;
    }

    public void setRoles(List<GuildRoleDocument> roles) {
        this.roles = roles;
    }

    public GuildRoleDocument getRole(SnowflakeId roleId) {
        return roles.stream()
                .filter(role -> role.getId().equals(roleId))
                .findFirst()
                .orElse(null);
    }

    public void addRole(GuildRoleDocument role) {
        roles.add(role);
    }

    public void removeRole(SnowflakeId roleId) {
        roles.removeIf(role -> role.getId().equals(roleId));
    }

    @Nullable
    public Long getDateDeleted() {
        return dateDeleted;
    }

    public void setDateDeleted(long dateDeleted) {
        this.dateDeleted = dateDeleted;
    }

    @Override
    public String toString() {
        return String.format("GuildDocument{id=%s, name='%s'}", id, name);
    }
}
