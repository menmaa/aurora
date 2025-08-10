/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.service;

import com.menmasystems.aurora.component.SnowflakeGenerator;
import com.menmasystems.aurora.dto.CreateGuildRequest;
import com.menmasystems.aurora.model.GuildDocument;
import com.menmasystems.aurora.model.RoleDocument;
import com.menmasystems.aurora.repository.GuildRepository;
import com.menmasystems.aurora.util.SnowflakeId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

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

    private RoleDocument createDefaultRole(SnowflakeId guildId) {
        return new RoleDocument(guildId, "@everyone");
    }
}
