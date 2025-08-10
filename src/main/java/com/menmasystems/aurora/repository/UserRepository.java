/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.repository;

import com.menmasystems.aurora.model.UserDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<UserDocument, Long> {
    Mono<Boolean> existsByUsername(String username);
    Mono<Boolean> existsByEmail(String email);

    Mono<UserDocument> findByEmail(String email);
}
