/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.database.repository;

import com.menmasystems.aurora.config.ReactiveMongoConfig;
import com.menmasystems.aurora.database.model.GuildDocument;
import com.menmasystems.aurora.database.model.GuildRoleDocument;
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

        GuildRoleDocument everyoneRole = new GuildRoleDocument(SnowflakeId.of(212714770883747840L), "@everyone");

        GuildRoleDocument role = new GuildRoleDocument(SnowflakeId.of(212714770883747841L), "Test Role 1");
        role.setPosition(1);

        GuildRoleDocument role2 = new GuildRoleDocument(SnowflakeId.of(212714770883747842L), "Test Role 2");
        role2.setPosition(2);

        GuildRoleDocument role3 = new GuildRoleDocument(SnowflakeId.of(212714770883747843L), "Test Role 3");
        role3.setPosition(3);

        GuildRoleDocument role4 = new GuildRoleDocument(SnowflakeId.of(212714770883747844L), "Test Role 4");
        role4.setPosition(4);

        GuildRoleDocument role5 = new GuildRoleDocument(SnowflakeId.of(212714770883747845L), "Test Role 5");
        role5.setPosition(5);

        // Insert sample guild
        testingGuild = new GuildDocument();
        testingGuild.setId(SnowflakeId.of(212714770883747840L));
        testingGuild.setName("Test Guild");
        testingGuild.setOwnerId(SnowflakeId.of(123456789012345678L));
        testingGuild.setRoles(List.of(everyoneRole, role, role2, role3, role4, role5));

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

        GuildDocument deletedGuild = guildRepository.findById(testingGuild.getId().id()).block();

        Assertions.assertNotNull(deletedGuild);
        Assertions.assertEquals(date, deletedGuild.getDateDeleted());
    }

    @Test
    void addRoleById_shouldAddRoleToGuildRolesArray() {
        GuildRoleDocument role = new GuildRoleDocument(SnowflakeId.of(212714770883747841L), "Test Role");
        role.setHoist(true);
        role.setColor(0xAFAFAF);

        guildRepository.addRoleByGuildId(testingGuild.getId(), role).block();

        Mono<GuildDocument> guild = guildRepository.findById(testingGuild.getId().id());

        StepVerifier.create(guild)
                .expectNextMatches(g -> g.getRoles().stream().anyMatch(r -> r.equals(role)))
                .verifyComplete();
    }

    @Test
    void findRolesByGuildId_shouldReturnGuild() {
        SnowflakeId roleId = SnowflakeId.of(212714770883747843L);
        Flux<GuildRoleDocument> roles = guildRepository.findRoleByGuildIdAndRoleId(212714770883747840L, roleId)
                .map(GuildDocument::getRoles)
                .flatMapMany(Flux::fromIterable);

        GuildRoleDocument expected = testingGuild.getRoles()
                .stream()
                .filter(role -> role.getId().equals(roleId))
                .findFirst()
                .orElseThrow();

        StepVerifier.create(roles)
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    void findRoleByGuildIdAndRoleId_shouldReturnOneGuildRole() {
        SnowflakeId roleId = SnowflakeId.of(212714770883747840L);
        Mono<GuildRoleDocument> role = guildRepository.findRoleByGuildIdAndRoleId(testingGuild.getId().id(), roleId)
                .map(guild -> guild.getRoles().getFirst());

        GuildRoleDocument expectedRole = testingGuild.getRoles()
                .stream()
                .filter(r -> r.getId().equals(roleId))
                .findFirst()
                .orElseThrow();

        StepVerifier.create(role)
                .expectNext(expectedRole)
                .verifyComplete();
    }

    @Test
    void incrGuildRolePositions_shouldIncrementGuildRolePositions() {
        guildRepository.incrGuildRolePositions(testingGuild.getId(), 1).block();

        Mono<GuildDocument> guild = guildRepository.findById(testingGuild.getId().id());

        StepVerifier.create(guild)
                .expectNextMatches(g -> {
                    GuildRoleDocument[] roles = g.getRoles().toArray(new GuildRoleDocument[0]);
                    return roles[0].getPosition() == 0
                            && roles[1].getPosition() == 2
                            && roles[2].getPosition() == 3
                            && roles[3].getPosition() == 4
                            && roles[4].getPosition() == 5
                            && roles[5].getPosition() == 6;
                })
                .verifyComplete();
    }

    @Test
    void decrGuildRolePositions_shouldDecrGuildRolePositions() {
        guildRepository.decrGuildRolePositions(testingGuild.getId(), 2).block();

        Mono<GuildDocument> guild = guildRepository.findById(testingGuild.getId().id());

        StepVerifier.create(guild)
                .expectNextMatches(g -> {
                    GuildRoleDocument[] roles = g.getRoles().toArray(new GuildRoleDocument[0]);
                    return roles[0].getPosition() == 0
                            && roles[1].getPosition() == 1
                            && roles[2].getPosition() == 1
                            && roles[3].getPosition() == 2
                            && roles[4].getPosition() == 3
                            && roles[5].getPosition() == 4;
                })
                .verifyComplete();
    }

    @Test
    void updateGuildRolePositions_shouldIncrementGuildRolePositionsBetweenSpecifiedPositions() {
        guildRepository.updateGuildRolePositionsInRange(testingGuild.getId(), 2, 4).block();

        GuildDocument guild = guildRepository.findById(testingGuild.getId().id()).block();
        Assertions.assertNotNull(guild);

        GuildRoleDocument[] roles = guild.getRoles().toArray(new GuildRoleDocument[0]);
        Assertions.assertEquals(0, roles[0].getPosition());
        Assertions.assertEquals(1, roles[1].getPosition());
        Assertions.assertEquals(2, roles[2].getPosition());
        Assertions.assertEquals(2, roles[3].getPosition());
        Assertions.assertEquals(3, roles[4].getPosition());
        Assertions.assertEquals(5, roles[5].getPosition());
    }

    @Test
    void updateGuildRolePositions_shouldDecrementGuildRolePositionsBetweenSpecifiedPositions() {
        guildRepository.updateGuildRolePositionsInRange(testingGuild.getId(), 5, 1).block();

        GuildDocument guild = guildRepository.findById(testingGuild.getId().id()).block();
        Assertions.assertNotNull(guild);

        GuildRoleDocument[] roles = guild.getRoles().toArray(new GuildRoleDocument[0]);
        Assertions.assertEquals(0, roles[0].getPosition());
        Assertions.assertEquals(2, roles[1].getPosition());
        Assertions.assertEquals(3, roles[2].getPosition());
        Assertions.assertEquals(4, roles[3].getPosition());
        Assertions.assertEquals(5, roles[4].getPosition());
        Assertions.assertEquals(5, roles[5].getPosition());
    }
}
