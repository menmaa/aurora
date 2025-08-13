/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.web.context;

import com.menmasystems.aurora.model.GuildDocument;
import com.menmasystems.aurora.model.GuildMemberDocument;

public record GuildRelatedRequestContext(GuildDocument guild, GuildMemberDocument member) {
}
