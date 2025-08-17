/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.service;

import com.menmasystems.aurora.component.SnowflakeGenerator;
import com.menmasystems.aurora.dto.guild.CreateGuildDto;
import com.menmasystems.aurora.dto.guild.GuildDto;
import com.menmasystems.aurora.database.model.GuildDocument;
import com.menmasystems.aurora.database.model.GuildRoleDocument;
import com.menmasystems.aurora.database.repository.GuildRepository;
import com.menmasystems.aurora.dto.guild.role.CreateRoleDto;
import com.menmasystems.aurora.dto.guild.role.RoleDto;
import com.menmasystems.aurora.exception.RoleNotFoundException;
import com.menmasystems.aurora.util.SnowflakeId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

/**
 * Service for managing guilds.
 *
 * @author Fotis Makris (Menma)
 */
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
    public Mono<GuildDocument> createGuild(SnowflakeId userId, CreateGuildDto request) {
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
        return guildRepository.findById(id.id())
                .filter(doc -> doc.getDateDeleted() == null);
    }

    public Mono<GuildDocument> updateGuild(SnowflakeId guildId, GuildDto request) {
        return guildRepository.updateGuildById(guildId, request);
    }

    public Mono<Void> deleteGuild(SnowflakeId guildId) {
        var date = Instant.now().toEpochMilli();
        return guildRepository.updateDateDeletedById(guildId.id(), date).then();
    }

    @Transactional
    public Mono<GuildRoleDocument> addRole(SnowflakeId guildId, CreateRoleDto request) {
        SnowflakeId roleId = snowflakeGenerator.generate();
        GuildRoleDocument role = new GuildRoleDocument(roleId, request.getName());

        return guildRepository.addRoleByGuildId(guildId, role)
                .flatMap(roleDoc -> guildRepository.incrGuildRolePositions(guildId, roleDoc.getPosition()))
                .thenReturn(role);
    }

    public Flux<GuildRoleDocument> getRoles(SnowflakeId guildId) {
        return guildRepository.findRolesByGuildId(guildId.id())
                .map(GuildDocument::getRoles)
                .flatMapMany(Flux::fromIterable);
    }

    public Mono<GuildRoleDocument> getRole(SnowflakeId guildId, SnowflakeId roleId) {
        return guildRepository.findRoleByGuildIdAndRoleId(guildId.id(), roleId)
                .map(guild -> guild.getRoles().getFirst())
                .switchIfEmpty(Mono.error(new RoleNotFoundException(roleId.id())));
    }

    public Mono<GuildRoleDocument> updateRole(GuildDocument guildDoc, SnowflakeId roleId, RoleDto request) {
        return guildRepository.updateRoleById(guildDoc.getId(), roleId, request)
                .switchIfEmpty(Mono.error(new RoleNotFoundException(roleId.id())));
    }

    @Transactional
    public Mono<Void> deleteRole(SnowflakeId guildId, SnowflakeId roleId) {
        if(guildId.equals(roleId))
            return Mono.error(new IllegalArgumentException("Cannot delete the @everyone role."));

        return guildRepository.deleteRoleByGuildIdAndRoleId(guildId, roleId)
                .flatMap(role -> guildRepository.decrGuildRolePositions(guildId, role.getPosition()));
    }

    private GuildRoleDocument createDefaultRole(SnowflakeId guildId) {
        return new GuildRoleDocument(guildId, "@everyone");
    }
}
