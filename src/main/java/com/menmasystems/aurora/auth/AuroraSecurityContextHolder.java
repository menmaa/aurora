/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.auth;

import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.function.Function;

public final class AuroraSecurityContextHolder {

    private static final Class<?> SECURITY_CONTEXT_KEY = AuroraSecurityContext.class;

    private AuroraSecurityContextHolder() {}

    public static Mono<AuroraSecurityContext> getContext() {
        return Mono.deferContextual(Mono::just)
                .cast(Context.class)
                .filter(AuroraSecurityContextHolder::hasSecurityContext)
                .flatMap(AuroraSecurityContextHolder::getSecurityContext);
    }

    private static boolean hasSecurityContext(Context context) {
        return context.hasKey(SECURITY_CONTEXT_KEY);
    }

    private static Mono<AuroraSecurityContext> getSecurityContext(Context context) {
        return context.<Mono<AuroraSecurityContext>>get(SECURITY_CONTEXT_KEY);
    }

    private static Function<Context, Context> clearContext() {
        return (context) -> context.delete(SECURITY_CONTEXT_KEY);
    }

    public static Context withSecurityContext(Mono<AuroraSecurityContext> securityContext) {
        return Context.of(SECURITY_CONTEXT_KEY, securityContext);
    }

    public static Context withAuthentication(AuroraAuthentication authentication) {
        return withSecurityContext(Mono.just(new AuroraSecurityContextImpl(authentication)));
    }
}
