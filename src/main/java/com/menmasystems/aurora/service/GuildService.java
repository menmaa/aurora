/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.service;

import com.menmasystems.aurora.dto.CreateGuildRequest;
import com.menmasystems.aurora.model.GuildDocument;
import com.menmasystems.aurora.model.GuildMemberDocument;
import com.menmasystems.aurora.model.RoleDocument;
import com.menmasystems.aurora.repository.GuildMemberRepository;
import com.menmasystems.aurora.repository.GuildRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class GuildService {

    private final GuildRepository guildRepository;
    private final GuildMemberRepository guildMemberRepository;

    public GuildService(GuildRepository guildRepository, GuildMemberRepository guildMemberRepository) {
        this.guildRepository = guildRepository;
        this.guildMemberRepository = guildMemberRepository;
    }

    @Transactional
    public Mono<GuildDocument> createGuild(String userId, CreateGuildRequest request) {
        GuildDocument guild = new GuildDocument();
        guild.setName(request.getName());
        guild.setIcon(request.getIcon());
        guild.setOwnerId(userId);
        guild.setRoles(List.of(createDefaultRole()));

        return guildRepository.save(guild)
                .flatMap(savedGuild -> addGuildMember(savedGuild.getId(), savedGuild.getOwnerId())
                        .thenReturn(savedGuild));
    }

    public Mono<GuildDocument> getGuildById(String id) {
        return guildRepository.findById(id);
    }

    public Mono<GuildMemberDocument> addGuildMember(String guildId, String userId) {
        return guildMemberRepository.save(new GuildMemberDocument(guildId, userId));
    }

    public Mono<GuildMemberDocument> getGuildMember(String guildId, String userId) {
        return guildMemberRepository.findByGuildIdAndUserId(guildId, userId);
    }

    public Mono<Boolean> isGuildMember(String guildId, String userId) {
        return guildMemberRepository.existsByGuildIdAndUserId(guildId, userId);
    }

    private RoleDocument createDefaultRole() {
        return new RoleDocument("@everyone");
    }
}
