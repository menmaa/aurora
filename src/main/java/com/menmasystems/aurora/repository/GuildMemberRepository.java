/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.repository;

import com.menmasystems.aurora.model.GuildMemberDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface GuildMemberRepository extends ReactiveMongoRepository<GuildMemberDocument, String> {
    Mono<GuildMemberDocument> findByGuildIdAndUserId(String guildId, String userId);
    Mono<Boolean> existsByGuildIdAndUserId(String guildId, String userId);
}
