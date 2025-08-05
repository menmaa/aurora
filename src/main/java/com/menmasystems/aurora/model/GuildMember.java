/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "guild_members")
public class GuildMember {

    private final String guildId;
    private String displayName;
    private String avatar;
    private final String userId;
    private List<String> roles;

    public GuildMember(String guildId, String userId) {
        this.guildId = guildId;
        this.userId = userId;
        this.roles = List.of();
    }

    public String getGuildId() {
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

    public String getUserId() {
        return userId;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
