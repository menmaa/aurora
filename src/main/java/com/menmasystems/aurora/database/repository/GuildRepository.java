/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.database.repository;

import com.menmasystems.aurora.database.model.GuildDocument;
import com.menmasystems.aurora.database.model.GuildRoleDocument;
import com.menmasystems.aurora.util.SnowflakeId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Update;
import reactor.core.publisher.Mono;

public interface GuildRepository extends ReactiveMongoRepository<GuildDocument, Long> {

    @Query("{ '_id' : ?0 }")
    @Update("{ '$set' : { 'dateDeleted' : ?1 } }")
    Mono<Void> updateDateDeletedById(long guildId, long dateDeleted);

    @Query("{ '_id' : ?0 }")
    @Update("{ '$push': { 'roles': ?1 } }")
    Mono<Void> addRoleById(long guildId, GuildRoleDocument roleDocument);

    @Query(value = "{ '_id' : ?0 }", fields = "{ '_id' : 0, 'roles': 1 }")
    Mono<GuildDocument> findRolesByGuildId(long guildId);

    @Query(value = "{ '_id' : ?0, 'roles.id' : ?1 }", fields = "{ '_id' : 0, 'roles.$': 1 }")
    Mono<GuildDocument> findRoleByGuildIdAndRoleId(long guildId, SnowflakeId roleId);
}