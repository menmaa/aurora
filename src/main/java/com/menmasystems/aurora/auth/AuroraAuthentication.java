/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.auth;

import com.menmasystems.aurora.exception.InvalidTokenSignatureException;
import com.menmasystems.aurora.util.SnowflakeId;
import reactor.core.publisher.Mono;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public record AuroraAuthentication(SnowflakeId userId, int timestamp) {
    // TODO Retrieve Key from AWS Secrets Manager or KMS (Probably KMS)
    private static final String SIGNING_KEY = "AuroraRestApplication";
    private static final int EPOCH = 1735689600;

    public String generateSignedToken() {
        byte[] userId = userId().toString().getBytes();
        byte[] timestampBytes = ByteBuffer.allocate(4).putInt(timestamp() - EPOCH).array();

        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        String encodedUserId = encoder.encodeToString(userId);
        String encodedTimestamp = encoder.encodeToString(timestampBytes);
        String encodedSignature = createHmac(encodedUserId + encodedTimestamp);

        return encodedUserId + "." + encodedTimestamp + "." + encodedSignature;
    }

    public static String generateSignedToken(SnowflakeId userId, int timestamp) {
        return new AuroraAuthentication(userId, timestamp).generateSignedToken();
    }

    public static Mono<AuroraAuthentication> verifySignedToken(String token) {
        if (token == null || token.isEmpty()) {
            return Mono.error(new InvalidTokenSignatureException());
        }

        if (!token.matches("^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+$")) {
            return Mono.error(new InvalidTokenSignatureException());
        }

        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return Mono.error(new InvalidTokenSignatureException());
        }

        String encodedUserId = parts[0];
        String encodedTimestamp = parts[1];
        String tokenSignature = parts[2];

        String payload = encodedUserId + encodedTimestamp;
        String expectedSignature = createHmac(payload);

        if (!secureEquals(tokenSignature, expectedSignature)) {
            return Mono.error(new InvalidTokenSignatureException());
        }

        Base64.Decoder decoder = Base64.getUrlDecoder();
        try {
            String decodedUserId = new String(decoder.decode(encodedUserId));
            SnowflakeId userId = new SnowflakeId(Long.parseLong(decodedUserId));
            int timestamp = EPOCH + ByteBuffer.wrap(decoder.decode(encodedTimestamp)).getInt();

            return Mono.just(new AuroraAuthentication(userId, timestamp));
        } catch (NumberFormatException e) {
            return Mono.error(new InvalidTokenSignatureException());
        }
    }

    private static String createHmac(String payload) {
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        String algorithm = "HmacSHA224";
        SecretKeySpec hmacKey = new SecretKeySpec(SIGNING_KEY.getBytes(), algorithm);

        try {
            Mac mac = Mac.getInstance(algorithm);
            mac.init(hmacKey);
            byte[] rawHmac = mac.doFinal(payload.getBytes());
            return encoder.encodeToString(rawHmac);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean secureEquals(String a, String b) {
        byte[] aBytes = a.getBytes();
        byte[] bBytes = b.getBytes();

        if (aBytes.length != bBytes.length) {
            return false;
        }

        int differences = 0;
        for (int i = 0; i < aBytes.length; i++) {
            differences |= aBytes[i] ^ bBytes[i];
        }

        return differences == 0;
    }
}
