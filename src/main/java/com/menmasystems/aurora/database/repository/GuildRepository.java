/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.database.repository;

import com.menmasystems.aurora.database.model.GuildDocument;
import com.menmasystems.aurora.util.SnowflakeId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface GuildRepository extends ReactiveMongoRepository<GuildDocument, Long>, GuildRepositoryEx {

    @Query("{ '_id' : ?0 }")
    @Update("{ '$set' : { 'dateDeleted' : ?1 } }")
    Mono<Void> updateDateDeletedById(long id, long dateDeleted);

    @Query(value = "{ '_id' : ?0 }", fields = "{ '_id' : 0, 'roles': 1 }")
    Mono<GuildDocument> findRolesByGuildId(long guildId);

    @Query(value = "{ '_id' : ?0, 'roles.id' : ?1 }", fields = "{ '_id' : 0, 'roles.$': 1 }")
    Mono<GuildDocument> findRoleByGuildIdAndRoleId(long guildId, SnowflakeId roleId);
}