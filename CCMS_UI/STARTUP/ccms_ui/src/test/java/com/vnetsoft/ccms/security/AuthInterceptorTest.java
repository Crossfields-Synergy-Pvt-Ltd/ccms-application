package com.vnetsoft.ccms.security;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class AuthInterceptorTest {

    @Test
    public void rejectsMissingToken() throws Exception {
        AuthInterceptor interceptor = new AuthInterceptor(new AuthTokenService());
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/node/list");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThat(interceptor.preHandle(request, response, new Object()), is(false));
        assertThat(response.getStatus(), is(HttpServletResponse.SC_UNAUTHORIZED));
    }

    @Test
    public void acceptsValidToken() throws Exception {
        AuthTokenService service = new AuthTokenService();
        AuthInterceptor interceptor = new AuthInterceptor(service);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/node/list");
        request.addHeader("Authorization", service.issue("admin@example.com"));

        assertThat(interceptor.preHandle(request, new MockHttpServletResponse(), new Object()), is(true));
        assertThat((String) request.getAttribute("authenticatedEmail"), is("admin@example.com"));
    }

    @Test
    public void allowsPublicMonitorDataWithoutToken() throws Exception {
        AuthInterceptor interceptor = new AuthInterceptor(new AuthTokenService());
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/dashboard/map_data");

        assertThat(interceptor.preHandle(request, new MockHttpServletResponse(), new Object()), is(true));
    }
}
