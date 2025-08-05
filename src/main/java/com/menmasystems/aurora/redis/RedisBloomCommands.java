/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.redis;

import io.lettuce.core.dynamic.Commands;
import io.lettuce.core.dynamic.annotation.Command;
import io.lettuce.core.dynamic.annotation.Param;

public interface RedisBloomCommands extends Commands {

    @Command("CF.RESERVE :key :capacity")
    String cfReserve(@Param("key") String key, @Param("capacity") long capacity);

    @Command("CF.ADD :key :value")
    Boolean cfAdd(@Param("key") String key, @Param("value") String value);

    @Command("CF.DEL :key: value")
    Boolean cfDel(@Param("key") String key, @Param("value") String value);

    @Command("CF.EXISTS :key :value")
    Boolean cfExists(@Param("key") String key, @Param("value") String value);
}
