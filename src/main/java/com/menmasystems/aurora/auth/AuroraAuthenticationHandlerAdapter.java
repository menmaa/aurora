/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.auth;

import com.menmasystems.aurora.annotation.Secured;
import com.menmasystems.aurora.auth.exception.UnauthenticatedUserException;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.HandlerAdapter;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuroraAuthenticationHandlerAdapter implements HandlerAdapter {

    private final RequestMappingHandlerAdapter delegate;

    public AuroraAuthenticationHandlerAdapter(RequestMappingHandlerAdapter delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean supports(Object handler) {
        return delegate.supports(handler);
    }

    @Override
    public Mono<HandlerResult> handle(ServerWebExchange exchange, Object handler) {
        if(handler instanceof HandlerMethod method) {
            Secured secured = method.getMethodAnnotation(Secured.class);
            if(secured == null) {
                secured = method.getBeanType().getAnnotation(Secured.class);
            }

            if(secured == null) {
                return delegate.handle(exchange, handler);
            }

            return AuroraSecurityContextHolder.getContext()
                    .map(AuroraSecurityContext::getAuthentication)
                    .switchIfEmpty(Mono.error(new UnauthenticatedUserException()))
                    .filter(AuroraAuthentication::isAuthenticated)
                    .switchIfEmpty(Mono.error(new UnauthenticatedUserException()))
                    .then(delegate.handle(exchange, handler));
        }
        return delegate.handle(exchange, handler);
    }
}
