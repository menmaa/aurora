/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.repository;

import com.menmasystems.aurora.model.GuildMember;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface GuildMemberRepository extends ReactiveMongoRepository<GuildMember, String> {
    Mono<GuildMember> findByGuildIdAndUserId(String guildId, String userId);
    Mono<Boolean> existsByGuildIdAndUserId(String guildId, String userId);
}
