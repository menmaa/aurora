/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.service;

import com.menmasystems.aurora.model.GuildDocument;
import com.menmasystems.aurora.model.GuildMemberDocument;
import com.menmasystems.aurora.model.RoleDocument;
import com.menmasystems.aurora.util.Permission;
import com.menmasystems.aurora.util.SnowflakeId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PermissionService {

    private final GuildService guildService;
    private final GuildMemberService guildMemberService;

    public PermissionService(GuildService guildService, GuildMemberService guildMemberService) {
        this.guildService = guildService;
        this.guildMemberService = guildMemberService;
    }

    public Mono<Boolean> hasGuildPermission(SnowflakeId userId, SnowflakeId guildId, Permission permission) {
        if(permission == Permission.NONE) {
            return Mono.just(true);
        }

        return getBasePermissions(userId, guildId)
                .any(p -> p == Permission.ADMINISTRATOR || p == permission);
    }

    public Mono<Boolean> hasGuildPermission(GuildDocument guild, GuildMemberDocument member, Permission permission) {
        if(permission == Permission.NONE) {
            return Mono.just(true);
        }

        return getBasePermissions(guild, member)
                .any(p -> p == Permission.ADMINISTRATOR || p == permission);
    }

    private Flux<Permission> getBasePermissions(GuildDocument guild, GuildMemberDocument member) {
        if (guild.getOwnerId().equals(member.getUserId())) {
            return Flux.just(Permission.ADMINISTRATOR);
        }

        Map<SnowflakeId, RoleDocument> roleMap = getRoleMap(guild);
        long everyonePerms = roleMap.get(guild.getId()).getPermissions();

        return guildMemberService.getRoles(guild.getId(), member.getUserId())
                .map(roleMap::get)
                .filter(Objects::nonNull)
                .map(RoleDocument::getPermissions)
                .reduce(everyonePerms, (a, b) -> a | b)
                .flatMapMany(this::permissionsToFlux);
    }

    private Flux<Permission> getBasePermissions(SnowflakeId userId, SnowflakeId guildId) {
        return guildService.getGuildById(guildId)
                .flatMapMany(guild -> {
                    if (guild.getOwnerId().equals(userId)) {
                        return Flux.just(Permission.ADMINISTRATOR);
                    }

                    Map<SnowflakeId, RoleDocument> roleMap = getRoleMap(guild);
                    long everyonePerms = roleMap.get(guildId).getPermissions();

                    return guildMemberService.getRoles(guildId, userId)
                            .map(roleMap::get)
                            .filter(Objects::nonNull)
                            .map(RoleDocument::getPermissions)
                            .reduce(everyonePerms, (a, b) -> a | b)
                            .flatMapMany(this::permissionsToFlux);
                });
    }

    private Map<SnowflakeId, RoleDocument> getRoleMap(GuildDocument guild) {
        return guild.getRoles()
                .stream()
                .collect(Collectors.toMap(RoleDocument::getId, r -> r));
    }

    private Flux<Permission> permissionsToFlux(long permissions) {
        if ((permissions & Permission.ADMINISTRATOR.getValue()) != 0) {
            return Flux.just(Permission.ADMINISTRATOR);
        }
        return Flux.fromArray(Permission.getPermissions(permissions));
    }
}
