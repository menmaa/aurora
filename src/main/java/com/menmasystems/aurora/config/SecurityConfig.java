/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.config;

import com.menmasystems.aurora.auth.AuroraAuthenticationWebFilter;
import com.menmasystems.aurora.auth.AuroraAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfig {
    private final AuroraAuthenticationWebFilter authFilter;
    private final AuroraAuthenticationEntryPoint authEntryPoint;

    public SecurityConfig(AuroraAuthenticationWebFilter authFilter, AuroraAuthenticationEntryPoint authEntryPoint) {
        this.authFilter = authFilter;
        this.authEntryPoint = authEntryPoint;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .anonymous(ServerHttpSecurity.AnonymousSpec::disable)
                .authorizeExchange(authorizer -> authorizer
                        .pathMatchers("/auth/register", "/auth/login").permitAll()
                        .anyExchange().authenticated())
                .exceptionHandling(handler -> handler.authenticationEntryPoint(authEntryPoint))
                .addFilterAt(authFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
