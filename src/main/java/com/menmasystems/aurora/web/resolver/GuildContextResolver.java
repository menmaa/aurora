/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.web.resolver;

import com.menmasystems.aurora.annotation.GuildActionRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.menmasystems.aurora.web.mapping.AuroraAttributeMapping.GUILD_CONTEXT;

@Component
public class GuildContextResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(parameter.getParameterType())
                && Objects.requireNonNull(parameter.getMethod()).isAnnotationPresent(GuildActionRequest.class);
    }

    @Override
    public Mono<Object> resolveArgument(MethodParameter parameter, BindingContext bindingContext, ServerWebExchange exchange) {
        return Mono.just(Objects.requireNonNull(exchange.getAttribute(GUILD_CONTEXT)));
    }
}