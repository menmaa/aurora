/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.controller;

import com.menmasystems.aurora.auth.AuroraAuthenticationToken;
import com.menmasystems.aurora.dto.CreateGuildRequest;
import com.menmasystems.aurora.dto.Guild;
import com.menmasystems.aurora.exception.ApiException;
import com.menmasystems.aurora.exception.ErrorCode;
import com.menmasystems.aurora.service.GuildMemberService;
import com.menmasystems.aurora.service.GuildService;
import com.menmasystems.aurora.util.SnowflakeId;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/guilds")
class GuildController {

    private final GuildService guildService;
    private final GuildMemberService guildMemberService;

    public GuildController(GuildService guildService, GuildMemberService guildMemberService) {
        this.guildService = guildService;
        this.guildMemberService = guildMemberService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Guild> createGuild(@AuthenticationPrincipal AuroraAuthenticationToken auth, @Valid @RequestBody CreateGuildRequest request) {
        return guildService.createGuild(auth.getPrincipal(), request)
                .map(Guild::new);
    }

    @GetMapping("/{guildId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Guild> getGuild(@AuthenticationPrincipal AuroraAuthenticationToken auth, @PathVariable SnowflakeId guildId) {
        return guildService.getGuildById(guildId)
                .switchIfEmpty(Mono.error(new ApiException(ErrorCode.GUILD_NOT_FOUND)))
                .flatMap(guild -> guildMemberService.isMember(guild.getId(), auth.getPrincipal())
                    .flatMap(isMember -> isMember
                            ? Mono.just(new Guild(guild))
                            : Mono.error(new ApiException(ErrorCode.GUILD_NOT_FOUND))));
    }
}
