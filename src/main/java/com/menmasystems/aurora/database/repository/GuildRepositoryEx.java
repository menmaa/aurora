/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.database.repository;

import com.menmasystems.aurora.database.model.GuildDocument;
import com.menmasystems.aurora.database.model.GuildRoleDocument;
import com.menmasystems.aurora.dto.guild.GuildDto;
import com.menmasystems.aurora.dto.guild.role.RoleDto;
import com.menmasystems.aurora.util.SnowflakeId;
import reactor.core.publisher.Mono;

public interface GuildRepositoryEx {

    Mono<GuildDocument> updateGuildById(SnowflakeId id, GuildDto guild);

    Mono<GuildRoleDocument> addRoleByGuildId(SnowflakeId id, GuildRoleDocument roleDocument);
    Mono<GuildRoleDocument> updateRoleById(SnowflakeId guildId, SnowflakeId roleId, RoleDto role);
    Mono<GuildRoleDocument> deleteRoleByGuildIdAndRoleId(SnowflakeId guildId, SnowflakeId roleId);

    Mono<Void> incrGuildRolePositions(SnowflakeId guildId, int position);
    Mono<Void> decrGuildRolePositions(SnowflakeId guildId, int position);
    Mono<Void> updateGuildRolePositionsInRange(SnowflakeId guildId, int from, int to);

}
