/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.dto.guild;

import com.menmasystems.aurora.dto.guild.role.RoleDto;
import com.menmasystems.aurora.database.model.GuildDocument;
import com.menmasystems.aurora.util.SnowflakeId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.List;

public class GuildDto {

    @Null
    private SnowflakeId id;

    @NotBlank(groups = CreateGuildRequest.class, message = "Guild name is required")
    private JsonNullable<String> name = JsonNullable.undefined();
    private JsonNullable<String> icon = JsonNullable.undefined();

    @Null
    private SnowflakeId ownerId;

    @Null
    private List<RoleDto> roles;

    public GuildDto(GuildDocument guild) {
        this.setId(guild.getId());
        this.setName(JsonNullable.of(guild.getName()));
        this.setIcon(JsonNullable.of(guild.getIcon()));
        this.setOwnerId(guild.getOwnerId());
        this.setRoles(guild.getRoles().stream().map(RoleDto::new).toList());
    }

    public SnowflakeId getId() {
        return id;
    }

    public void setId(SnowflakeId id) {
        this.id = id;
    }

    public JsonNullable<String> getName() {
        return name;
    }

    public void setName(JsonNullable<String> name) {
        this.name = name;
    }

    public JsonNullable<String> getIcon() {
        return icon;
    }

    public void setIcon(JsonNullable<String> icon) {
        this.icon = icon;
    }

    public SnowflakeId getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(SnowflakeId ownerId) {
        this.ownerId = ownerId;
    }

    public List<RoleDto> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDto> roles) {
        this.roles = roles;
    }
}
