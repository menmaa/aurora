/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.dto;

import jakarta.validation.constraints.*;

public class RegisterUserRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, message = "Username must be at least 3 characters long")
    @Size(max = 20, message = "Username cannot be more than 20 characters long")
    @Pattern(regexp = "^[a-z0-9_.]+$", message = "Username can only contain lowercase letters, numbers, underscores, and periods")
    private String username;

    @NotBlank(message = "Display name is required")
    @Size(max = 64, message = "Display name cannot be more than 64 characters long")
    private String display_name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$", message = "Password must contain at least one uppercase letter, one lowercase letter, and one number")
    private String password;

    @AssertTrue(message = "Consent is required")
    private boolean consent;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return display_name;
    }

    public void setDisplayName(String display_name) {
        this.display_name = display_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean hasConsented() {
        return consent;
    }

    public void setConsent(boolean consent) {
        this.consent = consent;
    }
}
