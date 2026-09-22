package com.vnetsoft.ccms.security;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/** Protects application APIs while leaving the public monitor APIs available. */
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthTokenService tokenService;

    public AuthInterceptor(AuthTokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        if (isPublic(path) || isStaticResource(path)) {
            return true;
        }
        Map<String, String> claims = tokenService.verify(request.getHeader("Authorization"));
        if (claims == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication is required");
            return false;
        }
        request.setAttribute("authenticatedEmail", claims.get("email"));
        return true;
    }

    private boolean isPublic(String path) {
        return "/superadmin/user/login".equals(path)
                || "/dashboard/map_data".equals(path)
                || "/dashboard/count".equals(path)
                || "/filter/get_mandal".equals(path)
                || "/filter/get_gp".equals(path)
                || "/filter/get_vilage".equals(path)
                || "/dcu/handshake_list".equals(path);
    }

    private boolean isStaticResource(String path) {
        return path.isEmpty() || "/".equals(path) || path.contains(".")
                || path.startsWith("/app/") || path.startsWith("/vendor/")
                || path.startsWith("/images/");
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) throws Exception {
    }
}
