/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.web.handler;

import com.menmasystems.aurora.annotation.GuildActionRequest;
import com.menmasystems.aurora.auth.AuroraAuthentication;
import com.menmasystems.aurora.exception.ApiException;
import com.menmasystems.aurora.exception.ErrorCode;
import com.menmasystems.aurora.database.model.GuildDocument;
import com.menmasystems.aurora.database.model.GuildMemberDocument;
import com.menmasystems.aurora.service.GuildMemberService;
import com.menmasystems.aurora.service.GuildService;
import com.menmasystems.aurora.service.PermissionService;
import com.menmasystems.aurora.util.Permission;
import com.menmasystems.aurora.util.SnowflakeId;
import com.menmasystems.aurora.web.context.GuildRelatedRequestContext;
import com.menmasystems.aurora.web.adapter.AbstractAnnotationHandler;
import com.menmasystems.aurora.web.adapter.AnnotationHandlersOrder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.menmasystems.aurora.web.mapping.AuroraAttributeMapping.*;

@Component
@Order(AnnotationHandlersOrder.AUTHORIZATION)
public class GuildActionRequestHandler extends AbstractAnnotationHandler<GuildActionRequest> {

    private final GuildService guildService;
    private final GuildMemberService guildMemberService;
    private final PermissionService permissionService;

    public GuildActionRequestHandler(GuildService guildService, GuildMemberService guildMemberService, PermissionService permissionService) {
        this.guildService = guildService;
        this.guildMemberService = guildMemberService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, HandlerMethod handlerMethod, GuildActionRequest annotation) {
        return getGuild(exchange, annotation)
                .zipWhen(guild -> getGuildMember(exchange, guild.getId()))
                .flatMap(tuple ->
                        assertPermissions(tuple.getT1(), tuple.getT2(), annotation.permission()).thenReturn(tuple)
                )
                .mapNotNull(tuple ->
                        exchange.getAttributes().put(GUILD_CONTEXT, new GuildRelatedRequestContext(tuple.getT1(), tuple.getT2()))
                )
                .then();
    }

    private Mono<GuildDocument> getGuild(ServerWebExchange exchange, GuildActionRequest annotation) {
        Map<String, String> params = exchange.getAttributeOrDefault(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, Map.of());
        String entityId = params.get(annotation.entityId());
        if(entityId.isEmpty() || !entityId.matches(SnowflakeId.SNOWFLAKE_REGEX_PATTERN)) {
            return Mono.error(new IllegalArgumentException("Missing Guild ID"));
        }

        return guildService.getGuildById(SnowflakeId.of(Long.parseLong(entityId)))
                .switchIfEmpty(Mono.error(new ApiException(ErrorCode.GUILD_NOT_FOUND)));
    }

    private Mono<GuildMemberDocument> getGuildMember(ServerWebExchange exchange, SnowflakeId guildId) {
        Object authAttr = exchange.getAttribute(AUTH_CONTEXT);
        if(!(authAttr instanceof AuroraAuthentication auth)) {
            return Mono.error(new IllegalStateException("Missing Aurora Authentication Attribute"));
        }

        return guildMemberService.getMember(guildId, auth.userId())
                .switchIfEmpty(Mono.error(new ApiException(ErrorCode.GUILD_NOT_FOUND)));
    }

    private Mono<Void> assertPermissions(GuildDocument guild, GuildMemberDocument member, Permission permission) {
        if(permission == Permission.NONE) {
            return Mono.empty();
        }

        return permissionService.hasGuildPermission(guild, member, permission)
                .flatMap(b -> b ? Mono.empty() : Mono.error(new ApiException(ErrorCode.MISSING_PERMISSIONS)));
    }
}
