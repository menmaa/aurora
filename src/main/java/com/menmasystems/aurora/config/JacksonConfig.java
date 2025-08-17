/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public JsonNullableModule jsonNullableModule() {
        return new JsonNullableModule();
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> builder.serializationInclusion(JsonInclude.Include.NON_ABSENT);
    }
}
