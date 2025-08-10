/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.util;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.lang.NonNull;

@JsonDeserialize(using = SnowflakeIdDeserializer.class)
public record SnowflakeId(long id) {

    public static SnowflakeId of(long id) {
        return new SnowflakeId(id);
    }

    @JsonValue
    @Override
    @NonNull
    public String toString() {
        return String.valueOf(id);
    }

    public boolean equals(SnowflakeId other) {
        return this.id == other.id;
    }
}
