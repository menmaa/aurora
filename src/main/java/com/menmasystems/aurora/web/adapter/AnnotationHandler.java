/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.web.adapter;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.lang.annotation.Annotation;

public interface AnnotationHandler<A extends Annotation> {

    Class<A> getAnnotationType();

    Mono<Void> handle(ServerWebExchange exchange, HandlerMethod handlerMethod, A annotation);
}
