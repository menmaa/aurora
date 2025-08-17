/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.controller;

import com.menmasystems.aurora.annotation.GuildActionRequest;
import com.menmasystems.aurora.annotation.SecuredRequest;
import com.menmasystems.aurora.dto.guild.role.CreateRoleDto;
import com.menmasystems.aurora.dto.guild.role.RoleDto;
import com.menmasystems.aurora.exception.RoleNotFoundException;
import com.menmasystems.aurora.service.GuildService;
import com.menmasystems.aurora.util.Permission;
import com.menmasystems.aurora.util.SnowflakeId;
import com.menmasystems.aurora.web.context.GuildRelatedRequestContext;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * REST Controller for managing guild roles.
 *
 * @author Fotis Makris (Menma)
 */
@RestController
@RequestMapping("/guilds/{guildId}/roles")
@SecuredRequest
class GuildRoleController {

    private final GuildService guildService;

    GuildRoleController(GuildService guildService) {
        this.guildService = guildService;
    }

    @PostMapping
    @GuildActionRequest(permission = Permission.MANAGE_ROLES)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<RoleDto> createGuildRole(
            @PathVariable SnowflakeId guildId,
            @Valid @RequestBody CreateRoleDto request) {
        return guildService.addRole(guildId, request).map(RoleDto::new);
    }

    @GetMapping
    @GuildActionRequest
    @ResponseStatus(HttpStatus.OK)
    public Flux<RoleDto> getRoles(
            @PathVariable SnowflakeId guildId,
            GuildRelatedRequestContext ctx
    ) {
        return Flux.fromStream(ctx.guild().getRoles().stream()).map(RoleDto::new);
    }

    @GetMapping("/{roleId}")
    @GuildActionRequest
    @ResponseStatus(HttpStatus.OK)
    public Mono<RoleDto> getRole(
            @PathVariable SnowflakeId guildId,
            @PathVariable SnowflakeId roleId,
            GuildRelatedRequestContext ctx
    ) {
        return Mono.justOrEmpty(ctx.guild().getRole(roleId))
                .switchIfEmpty(Mono.error(new RoleNotFoundException(roleId.id())))
                .map(RoleDto::new);
    }

    @PatchMapping("/{roleId}")
    @GuildActionRequest(permission = Permission.MANAGE_ROLES)
    @ResponseStatus(HttpStatus.OK)
    public Mono<RoleDto> updateRole(@PathVariable SnowflakeId guildId, @PathVariable SnowflakeId roleId) {
        return Mono.error(new IllegalStateException("Not yet implemented"));
    }

    @DeleteMapping("/{roleId}")
    @GuildActionRequest(permission = Permission.MANAGE_ROLES)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> deleteRole(@PathVariable SnowflakeId guildId, @PathVariable SnowflakeId roleId) {
        return Mono.error(new IllegalStateException("Not yet implemented"));
    }
}
