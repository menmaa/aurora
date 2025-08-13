/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.annotation;

import com.menmasystems.aurora.util.Permission;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@SecuredRequest
public @interface GuildActionRequest {
    @AliasFor("value")
    String entityId() default "guildId";

    @AliasFor("entityId")
    String value() default "guildId";

    Permission permission() default Permission.NONE;
}