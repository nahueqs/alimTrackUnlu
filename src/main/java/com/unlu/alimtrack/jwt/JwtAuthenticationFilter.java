package com.unlu.alimtrack.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();


    private final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/actuator/health/**",
            "/actuator/info",
            "/health",
            "/ping",
            "/",
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/auth/refresh-token",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/api/v1/public/**",
            "/ws/**",
            "/ws/info",
            "/websocket/**",
            "/sockjs-node/**"
    );

    private final List<String> PUBLIC_GET_ENDPOINTS = List.of(
            "/v1/producciones/**"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain
    ) throws ServletException, IOException {

        final String token = getTokenFromRequest(request);
        final String username;

        log.debug("Processing request to: {} {}", request.getMethod(), request.getRequestURI());

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            log.debug("Skipping JWT filter for OPTIONS request");
            filterChain.doFilter(request, response);
            return;
        }
        if (token == null) {
            log.debug("No JWT token found, continuing filter chain");
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("JWT token found, validating...");

        try {
            username = jwtService.getUsernameFromToken(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("User authenticated successfully: {}", username);
                } else {
                    log.debug("Invalid JWT token for user: {}", username);
                }
            }
        } catch (Exception e) {
            log.error("Error processing JWT token: ", e);
            // No lanzar excepción, continuar sin autenticación
        }


        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getServletPath();
        String method = request.getMethod();

        log.debug("Checking if should filter: {} {}", method, requestURI);

        for (String pattern : PUBLIC_ENDPOINTS) {
            if (pathMatcher.match(pattern, requestURI)) {
                log.debug("Skipping JWT filter for public endpoint: {} matches pattern: {}",
                        requestURI, pattern);
                return true;
            }
        }

        // Check if it's a public GET endpoint
        if ("GET".equalsIgnoreCase(method)) {
            for (String pattern : PUBLIC_GET_ENDPOINTS) {
                if (pathMatcher.match(pattern, requestURI)) {
                    log.debug("Skipping JWT filter for public GET endpoint: {} matches pattern: {}",
                            requestURI, pattern);
                    return true;
                }
            }
        }
        log.debug("JWT filter will be applied to: {} {}", method, requestURI);

        return false;
    }


    private String getTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
