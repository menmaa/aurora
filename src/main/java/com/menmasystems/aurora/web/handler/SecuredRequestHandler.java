/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.web.handler;

import com.menmasystems.aurora.annotation.SecuredRequest;
import com.menmasystems.aurora.auth.AuroraSecurityContext;
import com.menmasystems.aurora.auth.AuroraSecurityContextHolder;
import com.menmasystems.aurora.exception.UnauthenticatedUserException;
import com.menmasystems.aurora.web.adapter.AbstractAnnotationHandler;
import com.menmasystems.aurora.web.adapter.AnnotationHandlersOrder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.menmasystems.aurora.web.mapping.AuroraAttributeMapping.AUTH_CONTEXT;

@Component
@Order(AnnotationHandlersOrder.AUTHENTICATION)
public class SecuredRequestHandler extends AbstractAnnotationHandler<SecuredRequest> {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, HandlerMethod handlerMethod, SecuredRequest annotation) {
        return AuroraSecurityContextHolder.getContext()
                .map(AuroraSecurityContext::getAuthentication)
                .switchIfEmpty(Mono.error(new UnauthenticatedUserException()))
                .mapNotNull(auth -> exchange.getAttributes().put(AUTH_CONTEXT, auth))
                .then();
    }
}
