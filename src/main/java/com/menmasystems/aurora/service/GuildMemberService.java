/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.service;

import com.menmasystems.aurora.database.model.GuildMemberDocument;
import com.menmasystems.aurora.database.repository.GuildMemberRepository;
import com.menmasystems.aurora.util.SnowflakeId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class GuildMemberService {

    private final GuildMemberRepository guildMemberRepository;

    public GuildMemberService(GuildMemberRepository guildMemberRepository) {
        this.guildMemberRepository = guildMemberRepository;
    }

    public GuildMemberDocument addMember(SnowflakeId guildId, SnowflakeId userId) {
        return addMemberNoCache(guildId, userId).block();
    }

    public Mono<GuildMemberDocument> addMemberAsync(SnowflakeId guildId, SnowflakeId userId) {
        return Mono.fromCallable(() -> addMember(guildId, userId)).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<GuildMemberDocument> addMemberNoCache(SnowflakeId guildId, SnowflakeId userId) {
        return guildMemberRepository.save(new GuildMemberDocument(guildId, userId));
    }

    public Mono<GuildMemberDocument> getMember(SnowflakeId guildId, SnowflakeId userId) {
        return guildMemberRepository.findByGuildIdAndUserId(guildId, userId);
    }

    public Mono<Void> removeMember(SnowflakeId guildId, SnowflakeId userId) {
        return guildMemberRepository.deleteByGuildIdAndUserId(guildId, userId);
    }

    public Mono<Boolean> isMember(SnowflakeId guildId, SnowflakeId userId) {
        return getMember(guildId, userId).hasElement();
    }

    public Flux<SnowflakeId> getRoles(SnowflakeId guildId, SnowflakeId userId) {
        return getMember(guildId, userId)
                .flatMapIterable(GuildMemberDocument::getRoles);
    }

    public Mono<Void> removeAllMembers(SnowflakeId guildId) {
        return guildMemberRepository.deleteByGuildId(guildId);
    }
}
