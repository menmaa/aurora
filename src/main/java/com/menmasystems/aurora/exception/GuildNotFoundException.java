/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.exception;

public class GuildNotFoundException extends ApiException {
    public GuildNotFoundException(long guildId) {
        super(ErrorCode.GUILD_NOT_FOUND);
    }
}
