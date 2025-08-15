/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.service;

import com.menmasystems.aurora.component.SnowflakeGenerator;
import com.menmasystems.aurora.dto.guild.CreateGuildRequest;
import com.menmasystems.aurora.dto.guild.UpdateGuildRequest;
import com.menmasystems.aurora.database.model.GuildDocument;
import com.menmasystems.aurora.database.model.GuildRoleDocument;
import com.menmasystems.aurora.database.repository.GuildRepository;
import com.menmasystems.aurora.util.SnowflakeId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

@Service
public class GuildService {

    private final GuildRepository guildRepository;
    private final GuildMemberService guildMemberService;
    private final SnowflakeGenerator snowflakeGenerator;

    public GuildService(GuildRepository guildRepository, GuildMemberService guildMemberService, SnowflakeGenerator snowflakeGenerator) {
        this.guildRepository = guildRepository;
        this.guildMemberService = guildMemberService;
        this.snowflakeGenerator = snowflakeGenerator;
    }

    /**
     * Creates a new guild, owned by the given user.
     *
     * @param userId The user who created the request.
     * @param request The request object to create the guild.
     * @return The created guild database document.
     *
     * @implNote This method is transactional and the {@link GuildMemberService#addMember(SnowflakeId, SnowflakeId)}
     * which is part of the transaction is cached. If the transaction fails, the cache may NOT be cleared.
     * We need to figure out a way to clear the cache in such a case, without too much overhead.
     *
     * @see GuildMemberService#addMember(SnowflakeId, SnowflakeId)
     */
    @Transactional
    public Mono<GuildDocument> createGuild(SnowflakeId userId, CreateGuildRequest request) {
        GuildDocument guild = new GuildDocument();
        guild.setId(snowflakeGenerator.generate());
        guild.setName(request.getName());
        guild.setIcon(request.getIcon());
        guild.setOwnerId(userId);
        guild.setRoles(List.of(createDefaultRole(guild.getId())));

        return guildRepository.save(guild)
                .flatMap(doc -> guildMemberService.addMemberNoCache(doc.getId(), doc.getOwnerId())
                        .thenReturn(doc));
    }

    public Mono<GuildDocument> getGuildById(SnowflakeId id) {
        return guildRepository.findById(id.id());
    }

    public Mono<GuildDocument> updateGuild(GuildDocument guild, UpdateGuildRequest request) {
        if(request.getName().isPresent()) guild.setName(request.getName().get());
        if(request.getIcon().isPresent()) guild.setIcon(request.getIcon().get());
        return guildRepository.save(guild);
    }

    public Mono<Void> deleteGuild(SnowflakeId guildId) {
        var date = Instant.now().toEpochMilli();
        return guildRepository.updateDateDeletedById(guildId.id(), date).then();
    }

    private GuildRoleDocument createDefaultRole(SnowflakeId guildId) {
        return new GuildRoleDocument(guildId, "@everyone");
    }
}
