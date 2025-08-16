/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.database.repository;

import com.menmasystems.aurora.database.model.GuildDocument;
import com.menmasystems.aurora.database.model.GuildRoleDocument;
import com.menmasystems.aurora.dto.guild.GuildDto;
import com.menmasystems.aurora.dto.guild.role.RoleDto;
import com.menmasystems.aurora.util.SnowflakeId;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Mono;

@SuppressWarnings("unused")
public class GuildRepositoryImpl implements GuildRepositoryEx {

    private final ReactiveMongoTemplate mongoTemplate;

    public GuildRepositoryImpl(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Mono<GuildDocument> updateGuildById(SnowflakeId guildId, GuildDto guild) {
        Query query = Query.query(Criteria.where("_id").is(guildId.id()));
        Update update = new Update();

        if(guild.getName().isPresent()) update.set("name", guild.getName().get());
        if(guild.getIcon().isPresent()) update.set("icon", guild.getIcon().get());

        if(update.getUpdateObject().isEmpty()) return Mono.empty();

        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);

        return mongoTemplate
                .findAndModify(query, update, options, GuildDocument.class);
    }

    @Override
    public Mono<GuildRoleDocument> addRoleByGuildId(SnowflakeId guildId, GuildRoleDocument roleDocument) {
        Query query = Query.query(Criteria.where("_id").is(guildId.id()));
        Update update = new Update().push("roles", roleDocument);
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);

        return mongoTemplate
                .findAndModify(query, update, options, GuildDocument.class)
                .map(guildDocument -> guildDocument.getRole(roleDocument.getId()));
    }

    @Override
    public Mono<GuildRoleDocument> updateRoleById(SnowflakeId guildId, SnowflakeId roleId, RoleDto role) {
        Query query = Query.query(Criteria.where("_id").is(guildId.id()).and("roles._id").is(roleId.id()));
        Update update = new Update();

        if(role.getName().isPresent()) update.set("roles.$.name", role.getName().get());
        if(role.getPermissions().isPresent()) update.set("roles.$.permissions", role.getPermissions().get());
        if(role.getColor().isPresent()) update.set("roles.$.color", role.getColor().get());
        if(role.getHoist().isPresent()) update.set("roles.$.hoist", role.getHoist().get());
        if(role.getPosition().isPresent()) update.set("roles.$.position", role.getPosition().get());
        if(role.getMentionable().isPresent()) update.set("roles.$.mentionable", role.getMentionable().get());

        if(update.getUpdateObject().isEmpty()) return Mono.empty();

        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);

        return mongoTemplate
                .findAndModify(query, update, options, GuildDocument.class)
                .map(guildDocument -> guildDocument.getRole(roleId));
    }

    @Override
    public Mono<GuildRoleDocument> deleteRoleByGuildIdAndRoleId(SnowflakeId guildId, SnowflakeId roleId) {
        Query query = Query.query(Criteria.where("_id").is(guildId.id()).and("roles._id").is(roleId.id()));
        Update update = new Update().pull("roles", new org.bson.Document("_id", roleId.id()));
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(false);

        return mongoTemplate
                .findAndModify(query, update, GuildDocument.class)
                .map(guildDocument -> guildDocument.getRole(roleId));
    }

    @Override
    public Mono<Void> incrGuildRolePositions(SnowflakeId guildId, int position) {
        return adjustGuildRolePosition(guildId, position, 1);
    }

    @Override
    public Mono<Void> decrGuildRolePositions(SnowflakeId guildId, int position) {
        return adjustGuildRolePosition(guildId, position, -1);
    }

    @Override
    public Mono<Void> updateGuildRolePositionsInRange(SnowflakeId guildId, int from, int to) {
        if(from < 0 || to < 0) return Mono.error(new IllegalArgumentException("Position cannot be negative"));
        if(from == to) return Mono.empty();

        Query query = Query.query(Criteria.where("_id").is(guildId.id()));
        Update update = new Update();

        if(to > from) {
            update.inc("roles.$[elem].position", -1)
                    .filterArray(
                            new Criteria().andOperator(
                                    Criteria.where("elem.position").gt(from),
                                    Criteria.where("elem.position").lte(to)
                            )
                    );
        } else {
            update.inc("roles.$[elem].position", 1)
                    .filterArray(
                            new Criteria().andOperator(
                                    Criteria.where("elem.position").lt(from),
                                    Criteria.where("elem.position").gte(to)
                            )
                    );
        }

        return mongoTemplate
                .updateFirst(query, update, GuildDocument.class)
                .filter(result -> result.wasAcknowledged() && result.getModifiedCount() > 0)
                .switchIfEmpty(Mono.error(new RuntimeException("No roles were updated")))
                .then();
    }

    private Mono<Void> adjustGuildRolePosition(SnowflakeId guildId, int position, int increment) {
        Query query = Query.query(Criteria.where("_id").is(guildId.id()));
        Update update = new Update().inc("roles.$[elem].position", increment)
                .filterArray(Criteria.where("elem.position").gte(position));

        return mongoTemplate
                .updateFirst(query, update, GuildDocument.class)
                .filter(result -> result.wasAcknowledged() && result.getModifiedCount() > 0)
                .switchIfEmpty(Mono.error(new RuntimeException("No roles were updated")))
                .then();
    }
}
