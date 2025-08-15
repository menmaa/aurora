/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.database.model;

import com.menmasystems.aurora.util.SnowflakeId;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@EqualsAndHashCode
@Document(collection = "guild_members")
public class GuildMemberDocument {

    private final SnowflakeId guildId;
    private final SnowflakeId userId;
    private String displayName;
    private String avatar;
    private List<SnowflakeId> roles;

    public GuildMemberDocument(SnowflakeId guildId, SnowflakeId userId) {
        this.guildId = guildId;
        this.userId = userId;
        this.roles = List.of();
    }

    public SnowflakeId getGuildId() {
        return guildId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public SnowflakeId getUserId() {
        return userId;
    }

    public List<SnowflakeId> getRoles() {
        return roles;
    }

    public void setRoles(List<SnowflakeId> roles) {
        this.roles = roles;
    }
}
