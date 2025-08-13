/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.repository;

import com.menmasystems.aurora.model.GuildDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface GuildRepository extends ReactiveMongoRepository<GuildDocument, Long> {
    Mono<Long> findOwnerIdById(long guildId);
}
