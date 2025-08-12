/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.config;

import com.menmasystems.aurora.auth.AuroraAuthContextResolver;
import com.menmasystems.aurora.web.resolver.GuildContextResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;

@Configuration
public class WebFluxConfig implements WebFluxConfigurer {

    private final AuroraAuthContextResolver authContextResolver;
    private final GuildContextResolver guildContextResolver;

    public WebFluxConfig(AuroraAuthContextResolver authContextResolver, GuildContextResolver guildContextResolver) {
        this.authContextResolver = authContextResolver;
        this.guildContextResolver = guildContextResolver;
    }

    @Override
    public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
        configurer.addCustomResolver(authContextResolver);
        configurer.addCustomResolver(guildContextResolver);
    }
}
