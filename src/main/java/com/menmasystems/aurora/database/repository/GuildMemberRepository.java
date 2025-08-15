/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.database.repository;

import com.menmasystems.aurora.database.model.GuildMemberDocument;
import com.menmasystems.aurora.util.SnowflakeId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface GuildMemberRepository extends ReactiveMongoRepository<GuildMemberDocument, String> {
    Mono<GuildMemberDocument> findByGuildIdAndUserId(SnowflakeId guildId, SnowflakeId userId);
    Mono<Void> deleteByGuildIdAndUserId(SnowflakeId guildId, SnowflakeId userId);
    Mono<Void> deleteByGuildId(SnowflakeId guildId);
}
