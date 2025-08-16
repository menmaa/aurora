/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.dto.guild.role;

import com.menmasystems.aurora.database.model.GuildRoleDocument;
import com.menmasystems.aurora.util.SnowflakeId;
import jakarta.validation.constraints.*;
import org.openapitools.jackson.nullable.JsonNullable;

public class RoleDto {

    @Null(message = "Role ID cannot be provided when creating a role")
    private SnowflakeId id;

    @NotBlank(groups = CreateGuildRoleRequest.class, message = "Role name is required")
    @Size(max = 32, message = "Role name cannot be more than 32 characters long")
    private JsonNullable<String> name = JsonNullable.undefined();

    private JsonNullable<Long> permissions = JsonNullable.undefined();

    @Min(value = 0, message = "Color must be a valid RGB value")
    @Max(value = 0xFFFFFF, message = "Color must be a valid RGB value")
    private JsonNullable<Integer> color = JsonNullable.undefined();

    private JsonNullable<Boolean> hoist = JsonNullable.undefined();
    private JsonNullable<Integer> position = JsonNullable.undefined();

    @Null
    private Boolean managed;

    private JsonNullable<Boolean> mentionable;

    public RoleDto() {}

    public RoleDto(GuildRoleDocument role) {
        this.setId(role.getId());
        this.setName(JsonNullable.of(role.getName()));
        this.setPermissions(JsonNullable.of(role.getPermissions()));
        this.setColor(JsonNullable.of(role.getColor()));
        this.setHoist(JsonNullable.of(role.isHoist()));
        this.setPosition(JsonNullable.of(role.getPosition()));
        this.setManaged(role.isManaged());
        this.setMentionable(JsonNullable.of(role.isMentionable()));
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

    public JsonNullable<Long> getPermissions() {
        return permissions;
    }

    public void setPermissions(JsonNullable<Long> permissions) {
        this.permissions = permissions;
    }

    public JsonNullable<Integer> getColor() {
        return color;
    }

    public void setColor(JsonNullable<Integer> color) {
        this.color = color;
    }

    public JsonNullable<Boolean> getHoist() {
        return hoist;
    }

    public void setHoist(JsonNullable<Boolean> hoist) {
        this.hoist = hoist;
    }

    public JsonNullable<Integer> getPosition() {
        return position;
    }

    public void setPosition(JsonNullable<Integer> position) {
        this.position = position;
    }

    public Boolean getManaged() {
        return managed;
    }

    public void setManaged(Boolean managed) {
        this.managed = managed;
    }

    public JsonNullable<Boolean> getMentionable() {
        return mentionable;
    }

    public void setMentionable(JsonNullable<Boolean> mentionable) {
        this.mentionable = mentionable;
    }
}