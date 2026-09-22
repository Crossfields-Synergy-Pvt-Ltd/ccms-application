package com.vnetsoft.ccms.security;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

/** Issues and verifies short-lived, tamper-resistant login tokens. */
@Service
public class AuthTokenService {

    private static final long TOKEN_TTL_MILLIS = 8L * 60L * 60L * 1000L;
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final byte[] SECRET = createSecret();

    public String issue(String email) {
        long expiresAt = System.currentTimeMillis() + TOKEN_TTL_MILLIS;
        String payload = email + "|" + expiresAt;
        return encode(payload) + "." + encodeBytes(sign(payload));
    }

    public Map<String, String> verify(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        try {
            String[] parts = token.split("\\.", 2);
            if (parts.length != 2) {
                return null;
            }
            String payload = decode(parts[0]);
            byte[] suppliedSignature = Base64.getUrlDecoder().decode(parts[1]);
            byte[] expectedSignature = sign(payload);
            if (!java.security.MessageDigest.isEqual(suppliedSignature, expectedSignature)) {
                return null;
            }
            String[] values = payload.split("\\|", 2);
            if (values.length != 2 || values[0].trim().isEmpty()) {
                return null;
            }
            long expiresAt = Long.parseLong(values[1]);
            if (expiresAt <= System.currentTimeMillis()) {
                return null;
            }
            Map<String, String> claims = new HashMap<String, String>();
            claims.put("email", values[0]);
            claims.put("expiresAt", values[1]);
            return claims;
        } catch (RuntimeException e) {
            return null;
        }
    }

    private byte[] sign(String payload) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(SECRET, HMAC_ALGORITHM));
            return mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Unable to sign authentication token", e);
        }
    }

    private String encode(String value) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String encodeBytes(byte[] value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }

    private String decode(String value) {
        return new String(Base64.getUrlDecoder().decode(value), StandardCharsets.UTF_8);
    }

    private static byte[] createSecret() {
        byte[] secret = new byte[32];
        new SecureRandom().nextBytes(secret);
        return secret;
    }
}
