/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.web.adapter;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.HandlerAdapter;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.annotation.Annotation;
import java.util.List;

@Component
public class AnnotationHandlerAdapter implements HandlerAdapter {

    private final RequestMappingHandlerAdapter delegate;
    private final List<AnnotationHandler<?>> annotationHandlers;

    public AnnotationHandlerAdapter(RequestMappingHandlerAdapter delegate, List<AnnotationHandler<?>> annotationHandlers) {
        this.delegate = delegate;
        this.annotationHandlers = annotationHandlers;
    }

    @Override
    public boolean supports(Object handler) {
        return delegate.supports(handler);
    }

    @Override
    public Mono<HandlerResult> handle(ServerWebExchange exchange, Object handler) {
        if (!(handler instanceof HandlerMethod method)) {
            return delegate.handle(exchange, handler);
        }

        return Flux.fromIterable(annotationHandlers)
                .concatMap(handlerBean -> handleWithAnnotation(exchange, method, handlerBean))
                .then(delegate.handle(exchange, handler));
    }

    private <A extends Annotation> Mono<Void> handleWithAnnotation(
            ServerWebExchange exchange,
            HandlerMethod method,
            AnnotationHandler<A> handlerBean
    ) {
        return findAnnotation(method, handlerBean.getAnnotationType())
                .flatMap(annotation -> handlerBean.handle(exchange, method, annotation));
    }

    private <A extends Annotation> Mono<A> findAnnotation(HandlerMethod method, Class<A> annotationType) {
        return Mono.justOrEmpty(method.getMethodAnnotation(annotationType))
                .switchIfEmpty(Mono.justOrEmpty(method.getBeanType().getAnnotation(annotationType)));
    }
}
