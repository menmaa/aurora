/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.web.adapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class AbstractAnnotationHandler<A extends Annotation> implements AnnotationHandler<A> {
    private final Class<A> annotationType;

    @SuppressWarnings("unchecked")
    protected AbstractAnnotationHandler() {
        Type type = ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];

        if(type instanceof Class<?>) {
            this.annotationType = (Class<A>) type;
        } else {
            throw new IllegalArgumentException("Invalid annotation type");
        }
    }

    @Override
    public Class<A> getAnnotationType() {
        return annotationType;
    }
}
