/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.database.repository;

import com.menmasystems.aurora.config.ReactiveMongoConfig;
import com.menmasystems.aurora.model.GuildDocument;
import com.menmasystems.aurora.model.RoleDocument;
import com.menmasystems.aurora.repository.GuildRepository;
import com.menmasystems.aurora.util.SnowflakeId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;

@DataMongoTest
@Import(ReactiveMongoConfig.class)
@TestPropertySource(properties = {
        "spring.data.mongodb.database=aurora_${random.uuid}"
})
public class GuildRepositoryTest {

    @Autowired
    private GuildRepository guildRepository;

    private GuildDocument testingGuild;

    @BeforeEach
    void setUp() {
        guildRepository.deleteAll().block();

        // Insert sample guild
        testingGuild = new GuildDocument();
        testingGuild.setId(SnowflakeId.of(212714770883747840L));
        testingGuild.setName("Test Guild");
        testingGuild.setOwnerId(SnowflakeId.of(123456789012345678L));
        testingGuild.setRoles(List.of(new RoleDocument(SnowflakeId.of(212714770883747840L), "@everyone")));

        guildRepository.save(testingGuild).block();
    }

    @Test
    void findById_shouldReturnGuild() {
        Mono<GuildDocument> guild = guildRepository.findById(212714770883747840L);

        StepVerifier.create(guild)
                .expectNext(testingGuild)
                .verifyComplete();
    }

    @Test
    void updateDateDeletedById_shouldUpdateDateDeleted() {
        long date = Instant.now().toEpochMilli();
        guildRepository.updateDateDeletedById(testingGuild.getId().id(), date).block();

        GuildDocument guild = guildRepository.findById(testingGuild.getId().id()).block();
        GuildDocument deletedGuild = guildRepository.findDeletedGuildById(testingGuild.getId().id()).block();

        Assertions.assertNull(guild);
        Assertions.assertNotNull(deletedGuild);
        Assertions.assertEquals(date, deletedGuild.getDateDeleted());
    }

    @Test
    void findRolesByGuildId_shouldReturnGuild() {
        Flux<RoleDocument> roles = guildRepository.findRoleByIdAndRolesId(212714770883747840L, SnowflakeId.of(212714770883747840L))
                .map(GuildDocument::getRoles)
                .flatMapMany(Flux::fromIterable);

        StepVerifier.create(roles)
                .expectNext(testingGuild.getRoles().toArray(new RoleDocument[0]))
                .verifyComplete();
    }
}
