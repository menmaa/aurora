package com.menmasystems.aurora.util;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SnowflakeIdConverter implements Converter<String, SnowflakeId> {
    @Override
    public SnowflakeId convert(String source) {
        return new SnowflakeId(Long.parseLong(source));
    }
}