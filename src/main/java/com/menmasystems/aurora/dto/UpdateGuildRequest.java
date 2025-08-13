/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.dto;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class UpdateGuildRequest {

    private Optional<String> name = Optional.empty();
    private Optional<String> icon = Optional.empty();

    public Optional<String> getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Optional.of(name);
    }

    public Optional<String> getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = Optional.of(icon);
    }
}
