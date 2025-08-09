/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.repository;

import com.menmasystems.aurora.model.GuildDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface GuildRepository extends ReactiveMongoRepository<GuildDocument, String> {
}
