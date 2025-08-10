/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.util;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class SnowflakeIdReadConverter implements Converter<Long, SnowflakeId> {
    @Override
    public SnowflakeId convert(Long source) {
        return SnowflakeId.of(source);
    }
}
