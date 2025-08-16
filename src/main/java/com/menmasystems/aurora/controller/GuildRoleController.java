/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.controller;

import com.menmasystems.aurora.annotation.GuildActionRequest;
import com.menmasystems.aurora.dto.guild.role.CreateGuildRoleRequest;
import com.menmasystems.aurora.dto.guild.role.RoleDto;
import com.menmasystems.aurora.service.GuildService;
import com.menmasystems.aurora.util.SnowflakeId;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
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
@GuildActionRequest
class GuildRoleController {

    private final GuildService guildService;

    GuildRoleController(GuildService guildService) {
        this.guildService = guildService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<RoleDto> createGuildRole(
            @PathVariable SnowflakeId guildId,
            @Validated(CreateGuildRoleRequest.class) @RequestBody RoleDto request) {
        return guildService.addRole(guildId, request).map(RoleDto::new);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<RoleDto> getGuildRoles(@PathVariable SnowflakeId guildId) {
        return guildService.getRoles(guildId).map(RoleDto::new);
    }

    @GetMapping("/{roleId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<RoleDto> getRole(@PathVariable SnowflakeId guildId, @PathVariable SnowflakeId roleId) {
        return guildService.getRole(guildId, roleId).map(RoleDto::new);
    }

    @PatchMapping("/{roleId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<RoleDto> updateGuildRole(@PathVariable SnowflakeId guildId, @PathVariable SnowflakeId roleId) {
        return Mono.error(new IllegalStateException("Not yet implemented"));
    }

    @DeleteMapping("/{roleId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> deleteRole(@PathVariable SnowflakeId guildId, @PathVariable SnowflakeId roleId) {
        return Mono.error(new IllegalStateException("Not yet implemented"));
    }
}
