/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.util;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class SnowflakeIdWriteConverter implements Converter<SnowflakeId, Long> {
    @Override
    public Long convert(SnowflakeId source) {
        return source.id();
    }
}
