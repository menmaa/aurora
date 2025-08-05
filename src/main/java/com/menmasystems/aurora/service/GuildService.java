/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.service;

import com.menmasystems.aurora.dto.CreateGuildRequest;
import com.menmasystems.aurora.model.Guild;
import com.menmasystems.aurora.model.GuildMember;
import com.menmasystems.aurora.model.Role;
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
    public Mono<Guild> createGuild(String userId, CreateGuildRequest request) {
        Guild guild = new Guild();
        guild.setName(request.getName());
        guild.setIcon(request.getIcon());
        guild.setOwnerId(userId);
        guild.setRoles(List.of(createDefaultRole()));

        return guildRepository.save(guild)
                .flatMap(savedGuild -> addGuildMember(savedGuild.getId(), savedGuild.getOwnerId())
                        .thenReturn(savedGuild));
    }

    public Mono<Guild> getGuildById(String id) {
        return guildRepository.findById(id);
    }

    public Mono<GuildMember> addGuildMember(String guildId, String userId) {
        return guildMemberRepository.save(new GuildMember(guildId, userId));
    }

    public Mono<GuildMember> getGuildMember(String guildId, String userId) {
        return guildMemberRepository.findByGuildIdAndUserId(guildId, userId);
    }

    public Mono<Boolean> isGuildMember(String guildId, String userId) {
        return guildMemberRepository.existsByGuildIdAndUserId(guildId, userId);
    }

    private Role createDefaultRole() {
        return new Role("@everyone");
    }
}
