/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.util;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.lang.NonNull;

@JsonDeserialize(using = SnowflakeIdDeserializer.class)
public record SnowflakeId(long id) {

    public static final String SNOWFLAKE_REGEX_PATTERN = "^[0-9]+$";

    public static SnowflakeId of(long id) {
        return new SnowflakeId(id);
    }

    @JsonValue
    @Override
    @NonNull
    public String toString() {
        return String.valueOf(id);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof SnowflakeId(long id1))) {
            return false;
        }

        return this.id == id1;
    }
}
