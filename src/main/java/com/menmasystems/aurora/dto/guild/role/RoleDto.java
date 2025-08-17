/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.dto.guild.role;

import com.menmasystems.aurora.database.model.GuildRoleDocument;
import com.menmasystems.aurora.util.SnowflakeId;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;

@Data
@NoArgsConstructor
public class RoleDto {

    @Null(message = "Role ID cannot be provided when creating a role")
    private SnowflakeId id;

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
}