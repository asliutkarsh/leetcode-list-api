package com.leetcodeapi.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**


 */

/**
 * <h2>Description</h2>
 * This class implements the Spring Security AuthenticationEntryPoint interface and provides a custom implementation for handling authentication exceptions.
 * It is used to send an error response to the client with a HTTP status code of <b>403 (FORBIDDEN)</b> when authentication fails.
 * It is annotated with the <b>@Component</b> annotation to allow Spring to automatically detect and manage its lifecycle.
 * @author Utkarsh Jaiswal
 * @version 1.0
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN,"Access Denied");
    }
}
