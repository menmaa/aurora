/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.auth;

import com.menmasystems.aurora.exception.UnauthenticatedUserException;
import com.menmasystems.aurora.service.SessionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class AuroraAuthenticationWebFilter implements WebFilter {

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

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String tokenStr = authHeader.substring(7);
        return AuroraAuthenticationToken.verifySignedToken(tokenStr)
                .flatMap(auth -> sessionService.isSessionValid(auth)
                    .flatMap(valid -> valid
                            ? Mono.just(auth)
                            : Mono.error(new UnauthenticatedUserException())
                    ))
                .flatMap(auth -> chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
                .onErrorResume(ex -> chain.filter(exchange));
    }
}
