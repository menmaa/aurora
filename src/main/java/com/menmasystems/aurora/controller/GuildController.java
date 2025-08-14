/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.controller;

import com.menmasystems.aurora.annotation.AuthContext;
import com.menmasystems.aurora.annotation.GuildActionRequest;
import com.menmasystems.aurora.annotation.SecuredRequest;
import com.menmasystems.aurora.auth.AuroraAuthentication;
import com.menmasystems.aurora.dto.guild.CreateGuildRequest;
import com.menmasystems.aurora.dto.guild.GuildDto;
import com.menmasystems.aurora.dto.guild.UpdateGuildRequest;
import com.menmasystems.aurora.exception.ApiException;
import com.menmasystems.aurora.exception.ErrorCode;
import com.menmasystems.aurora.service.GuildMemberService;
import com.menmasystems.aurora.service.GuildService;
import com.menmasystems.aurora.util.Permission;
import com.menmasystems.aurora.util.SnowflakeId;
import com.menmasystems.aurora.web.context.GuildRelatedRequestContext;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/guilds")
@SecuredRequest
class GuildController {

    private final GuildService guildService;
    private final GuildMemberService guildMemberService;

    GuildController(GuildService guildService, GuildMemberService guildMemberService) {
        this.guildService = guildService;
        this.guildMemberService = guildMemberService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<GuildDto> createGuild(@AuthContext AuroraAuthentication auth, @Valid @RequestBody CreateGuildRequest request) {
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
            GuildRelatedRequestContext ctx,
            @PathVariable SnowflakeId guildId,
            @Valid @RequestBody UpdateGuildRequest request) {
        return guildService.updateGuild(ctx.guild(), request).map(GuildDto::new);
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
