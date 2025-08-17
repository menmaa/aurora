/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.dto.guild.role;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateRoleDto {

    @NotBlank(message = "Role name is required")
    private String name;

    private long permissions;

    @Min(value = 0, message = "Color must be a valid RGB value")
    @Max(value = 0xFFFFFF, message = "Color must be a valid RGB value")
    private int color;

    private boolean hoist;
    private int position;
    private boolean mentionable;
}