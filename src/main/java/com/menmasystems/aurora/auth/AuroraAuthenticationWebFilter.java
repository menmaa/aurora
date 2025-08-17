/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.auth;

import com.menmasystems.aurora.exception.UnauthenticatedUserException;
import com.menmasystems.aurora.service.SessionService;
import com.menmasystems.aurora.web.filter.SecurityWebFiltersOrder;
import org.springframework.boot.web.reactive.filter.OrderedWebFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class AuroraAuthenticationWebFilter implements OrderedWebFilter {

    private final SessionService sessionService;

    public AuroraAuthenticationWebFilter(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    @NonNull
    public Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        if (HttpMethod.OPTIONS.equals(exchange.getRequest().getMethod())) {
            return chain.filter(exchange);
        }

        String token = getAuthTokenFromHeader(exchange);
        if (token == null) {
            return chain.filter(exchange);
        }

        return AuroraAuthentication.verifySignedToken(token)
                .filterWhen(sessionService::isSessionValid)
                .switchIfEmpty(Mono.error(new UnauthenticatedUserException()))
                .flatMap(auth -> chain.filter(exchange)
                        .contextWrite(AuroraSecurityContextHolder.withAuthentication(auth)))
                .onErrorResume(ex -> chain.filter(exchange));
    }

    private String getAuthTokenFromHeader(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        return authHeader.substring(7);
    }

    @Override
    public int getOrder() {
        return SecurityWebFiltersOrder.AUTHENTICATION.getOrder();
    }
}
