/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.controller;

import com.menmasystems.aurora.annotation.AuthContext;
import com.menmasystems.aurora.annotation.GuildActionRequest;
import com.menmasystems.aurora.annotation.SecuredRequest;
import com.menmasystems.aurora.auth.AuroraAuthentication;
import com.menmasystems.aurora.dto.guild.CreateGuildDto;
import com.menmasystems.aurora.dto.guild.GuildDto;
import com.menmasystems.aurora.exception.ApiException;
import com.menmasystems.aurora.exception.ErrorCode;
import com.menmasystems.aurora.service.GuildService;
import com.menmasystems.aurora.util.Permission;
import com.menmasystems.aurora.util.SnowflakeId;
import com.menmasystems.aurora.web.context.GuildRelatedRequestContext;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * REST Controller for managing guilds.
 *
 * @author Fotis Makris (Menma)
 */
@RestController
@RequestMapping("/guilds")
@SecuredRequest
class GuildController {

    private final GuildService guildService;

    GuildController(GuildService guildService) {
        this.guildService = guildService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<GuildDto> createGuild(
            @AuthContext AuroraAuthentication auth,
            @Valid @RequestBody CreateGuildDto request
    ) {
        return guildService.createGuild(auth.userId(), request).map(GuildDto::new);
    }

    @GetMapping("/{guildId}")
    @GuildActionRequest
    @ResponseStatus(HttpStatus.OK)
    public Mono<GuildDto> getGuild(GuildRelatedRequestContext ctx, @PathVariable SnowflakeId guildId) {
        return Mono.just(ctx.guild()).map(GuildDto::new);
    }

    @PatchMapping("/{guildId}")
    @GuildActionRequest(permission = Permission.MANAGE_GUILD)
    @ResponseStatus(HttpStatus.OK)
    public Mono<GuildDto> updateGuild(
            @PathVariable SnowflakeId guildId,
            @Valid @RequestBody GuildDto request) {
        return guildService.updateGuild(guildId, request).map(GuildDto::new);
    }

    @DeleteMapping("/{guildId}")
    @GuildActionRequest
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> deleteGuild(@PathVariable SnowflakeId guildId, GuildRelatedRequestContext ctx) {
        if(!ctx.guild().getOwnerId().equals(ctx.member().getUserId())) {
            return Mono.error(new ApiException(ErrorCode.MISSING_ACCESS));
        }

        return guildService.deleteGuild(ctx.guild().getId());
    }
}
