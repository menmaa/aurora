/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.service;

import com.menmasystems.aurora.component.SnowflakeGenerator;
import com.menmasystems.aurora.dto.guild.role.CreateGuildRoleRequest;
import com.menmasystems.aurora.database.model.GuildDocument;
import com.menmasystems.aurora.database.model.GuildRoleDocument;
import com.menmasystems.aurora.database.repository.GuildRepository;
import com.menmasystems.aurora.util.SnowflakeId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class GuildRoleService {

    private final GuildRepository guildRepository;
    private final SnowflakeGenerator snowflakeGenerator;

    public GuildRoleService(GuildRepository guildRepository, SnowflakeGenerator snowflakeGenerator) {
        this.guildRepository = guildRepository;
        this.snowflakeGenerator = snowflakeGenerator;
    }

    public Mono<GuildRoleDocument> addRole(SnowflakeId guildId, CreateGuildRoleRequest request) {
        SnowflakeId roleId = snowflakeGenerator.generate();
        GuildRoleDocument role = new GuildRoleDocument(roleId, request);

        return guildRepository.addRoleById(guildId.id(), role).thenReturn(role);
    }

    public Flux<GuildRoleDocument> getRoles(SnowflakeId guildId) {
        return guildRepository.findRolesByGuildId(guildId.id())
                .map(GuildDocument::getRoles)
                .flatMapMany(Flux::fromIterable);
    }

    public Mono<GuildRoleDocument> getRole(SnowflakeId guildId, SnowflakeId roleId) {
        return guildRepository.findRoleByGuildIdAndRoleId(guildId.id(), roleId)
                .map(guild -> guild.getRoles().getFirst());
    }
}
