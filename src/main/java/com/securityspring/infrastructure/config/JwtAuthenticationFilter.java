package com.securityspring.infrastructure.config;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.securityspring.application.service.api.JwtServiceApi;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtServiceApi jwtService;

    private final CustomUserDetailsService userDetailsService;

    private final ApplicationEventPublisher eventPublisher;

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(final JwtServiceApi jwtService,
                                   final CustomUserDetailsService userDetailsService, ApplicationEventPublisher eventPublisher) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "authorization, content-type, xsrf-token");
            response.addHeader("Access-Control-Expose-Headers", "xsrf-token");
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        final String path = request.getRequestURI();
        if (path.startsWith("/api/v1/login") ||
                path.startsWith("/api/v1/auth") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/swagger-ui.html") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-resources") ||
                path.startsWith("/webjars") ||
                path.startsWith("/actuator/health") ||
                path.startsWith("/actuator/info")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        if (Objects.isNull(authHeader) || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        String username;
        try {
            username = jwtService.extractUsername(jwt);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            LOGGER.warn("Expired JWT token: {}", e.getMessage());
            response.setHeader("Token-Expired", "true");
            response.setHeader("WWW-Authenticate", "Bearer error=\"invalid_token\", error_description=\"The access token expired\"");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        } catch (io.jsonwebtoken.JwtException e) {
            LOGGER.warn("Invalid JWT token: {}", e.getMessage());
            response.setHeader("WWW-Authenticate", "Bearer error=\"invalid_token\", error_description=\"Invalid token\"");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        } catch (Exception e) {
            LOGGER.error("Error while processing JWT token: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (Objects.nonNull(username) && Objects.isNull(SecurityContextHolder.getContext().getAuthentication())) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(jwt, userDetails)) {

                String roleClaim = jwtService.extractClaimByName(jwt, "role");

                if (Objects.nonNull(roleClaim) && !roleClaim.trim().isEmpty()) {
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + roleClaim.trim().toUpperCase());

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    List.of(authority)
                            );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    LOGGER.info("User '{}' successfully authenticated with role {}", username, authority.getAuthority());
                } else {
                    LOGGER.warn("No role found in JWT for user '{}'", username);
                }

                final AuthenticationSuccessEvent successEvent =
                        new AuthenticationSuccessEvent(SecurityContextHolder.getContext().getAuthentication());
                this.eventPublisher.publishEvent(successEvent);
            } else {
                LOGGER.warn("Invalid JWT token for user '{}'", username);
            }
        }

        filterChain.doFilter(request, response);
    }
}
