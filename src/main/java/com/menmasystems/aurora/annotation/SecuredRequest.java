/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.annotation;

import java.lang.annotation.*;

/**
 * Annotation to indicate that the annotated method or class requires user authentication.
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SecuredRequest {
}
