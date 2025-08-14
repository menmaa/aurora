/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.controller;

import com.menmasystems.aurora.annotation.GuildActionRequest;
import com.menmasystems.aurora.dto.guild.role.RoleDto;
import com.menmasystems.aurora.dto.guild.role.CreateGuildRoleRequest;
import com.menmasystems.aurora.service.GuildRoleService;
import com.menmasystems.aurora.util.SnowflakeId;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/guilds/{guildId}/roles")
@GuildActionRequest
class GuildRoleController {

    private final GuildRoleService guildRoleService;

    GuildRoleController(GuildRoleService guildRoleService) {
        this.guildRoleService = guildRoleService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<RoleDto> createGuildRole(
            @PathVariable SnowflakeId guildId,
            @Valid @RequestBody CreateGuildRoleRequest request) {
        return guildRoleService.addRole(guildId, request).map(RoleDto::new);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<RoleDto> getGuildRoles(@PathVariable SnowflakeId guildId) {
        return guildRoleService.getRoles(guildId).map(RoleDto::new);
    }

    @GetMapping("/{roleId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<RoleDto> getRole(@PathVariable SnowflakeId guildId, @PathVariable SnowflakeId roleId) {
        return Mono.error(new IllegalStateException("Not yet implemented"));
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
