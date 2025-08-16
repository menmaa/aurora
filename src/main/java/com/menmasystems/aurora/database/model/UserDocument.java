/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.database.model;

import com.menmasystems.aurora.util.SnowflakeId;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Database document model for storing user information.
 *
 * @author Fotis Makris (Menma)
 */
@EqualsAndHashCode
@Document(collection = "users")
public class UserDocument {
    @Id
    private long id;
    private String username;
    private String display_name;
    private String email;
    private String password;

    private String discriminator;
    private String avatar;
    private boolean bot;
    private boolean mfa_enabled;
    private boolean verified;

    private long flags;
    private long premium_type;
    private long public_flags;

    public UserDocument() {}

    public SnowflakeId getId() {
        return SnowflakeId.of(id);
    }

    public void setId(SnowflakeId id) {
        this.id = id.id();
    }

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

    public String getDiscriminator() {
        return discriminator;
    }

    public void setDiscriminator(String discriminator) {
        this.discriminator = discriminator;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isBot() {
        return bot;
    }

    public void setBot(boolean bot) {
        this.bot = bot;
    }

    public boolean isMfaEnabled() {
        return mfa_enabled;
    }

    public void setMfaEnabled(boolean mfa_enabled) {
        this.mfa_enabled = mfa_enabled;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public long getFlags() {
        return flags;
    }

    public void setFlags(long flags) {
        this.flags = flags;
    }

    public long getPremiumType() {
        return premium_type;
    }

    public void setPremiumType(long premium_type) {
        this.premium_type = premium_type;
    }

    public long getPublicFlags() {
        return public_flags;
    }

    public void setPublicFlags(long public_flags) {
        this.public_flags = public_flags;
    }
}
