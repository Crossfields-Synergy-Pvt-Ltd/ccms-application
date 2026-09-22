package com.vnetsoft.ccms.security;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class AuthTokenServiceTest {

    @Test
    public void issuedTokenContainsAuthenticatedEmail() {
        AuthTokenService service = new AuthTokenService();

        assertThat(service.verify(service.issue("admin@example.com")).get("email"), is("admin@example.com"));
    }

    @Test
    public void tamperedTokenIsRejected() {
        AuthTokenService service = new AuthTokenService();
        String token = service.issue("admin@example.com");

        assertNull(service.verify(token + "tampered"));
    }

    @Test
    public void malformedTokenIsRejected() {
        assertNull(new AuthTokenService().verify("not-a-token"));
    }
}
