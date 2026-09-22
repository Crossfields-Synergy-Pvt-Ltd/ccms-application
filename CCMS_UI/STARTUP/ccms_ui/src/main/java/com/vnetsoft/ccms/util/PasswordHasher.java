package com.vnetsoft.ccms.util;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Password hashing without storing or comparing passwords in clear text. */
public final class PasswordHasher {

    private static final String PREFIX = "{PBKDF2}";
    private static final int ITERATIONS = 120000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    private PasswordHasher() {
    }

    public static String hash(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password is required");
        }
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        byte[] derived = derive(password, salt, ITERATIONS);
        return PREFIX + ITERATIONS + "$" + Base64.getEncoder().encodeToString(salt)
                + "$" + Base64.getEncoder().encodeToString(derived);
    }

    public static boolean matches(String password, String stored) {
        if (password == null || stored == null) {
            return false;
        }
        if (!stored.startsWith(PREFIX)) {
            // Compatibility for users inserted by the existing seed script. A successful
            // legacy login is upgraded by UserController before the response is returned.
            return MessageDigest.isEqual(password.getBytes(StandardCharsets.UTF_8),
                    stored.getBytes(StandardCharsets.UTF_8));
        }
        try {
            String[] parts = stored.substring(PREFIX.length()).split("\\$", 3);
            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] expected = Base64.getDecoder().decode(parts[2]);
            byte[] actual = derive(password, salt, iterations);
            return MessageDigest.isEqual(expected, actual);
        } catch (RuntimeException e) {
            return false;
        }
    }

    public static boolean isLegacy(String stored) {
        return stored != null && !stored.startsWith(PREFIX);
    }

    private static byte[] derive(String password, byte[] salt, int iterations) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, KEY_LENGTH);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Unable to hash password", e);
        }
    }
}
