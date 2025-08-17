/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.dto.guild;

import com.menmasystems.aurora.dto.guild.role.RoleDto;
import com.menmasystems.aurora.database.model.GuildDocument;
import com.menmasystems.aurora.util.SnowflakeId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.List;

@Data
@NoArgsConstructor
public class GuildDto {

    @Null
    private SnowflakeId id;

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
}
