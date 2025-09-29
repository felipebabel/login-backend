package com.securityspring.infrastructure.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class SecurityFilterConfig extends OncePerRequestFilter {

    @Getter
    private final ProjectProperties projectProperties;

    public SecurityFilterConfig(ProjectProperties projectProperties) {
        this.projectProperties = projectProperties;
    }

    static final Logger LOGGER = LoggerFactory.getLogger(SecurityFilterConfig.class);


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Correctly handle CORS preflight requests first
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "authorization, content-type, xsrf-token");
            response.addHeader("Access-Control-Expose-Headers", "xsrf-token");
            response.setStatus(HttpServletResponse.SC_OK);
            return; // Important: Don't proceed with the filter chain for OPTIONS requests
        }

        // Now, handle the actual request (GET, POST, DELETE, etc.)
        String token = request.getHeader("Authorization");
        var authentication = new UsernamePasswordAuthenticationToken("admin", null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // This header is also needed for the actual request, not just preflight

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "authorization, content-type, xsrf-token");
        // Your token validation logic goes here
        // ... (uncomment your token validation code) ...

        filterChain.doFilter(request, response);
//        if (!request.getMethod().equals("OPTIONS")) {
//            String token = request.getHeader("Authorization");
//            var authentication = new UsernamePasswordAuthenticationToken("admin", null, null);
//                        SecurityContextHolder.getContext().setAuthentication(authentication);
////            if (token != null && token.startsWith("Bearer ")) {
////                token = token.substring(7);
////                final DecodedJWT tokenValid = validateToken(token);
////                if (Objects.nonNull(tokenValid)) {
////                    if (validateTokenTime(tokenValid.getIssuedAt())) {
////                        LOGGER.info("Token valid.");
////                        var authentication = new UsernamePasswordAuthenticationToken("admin", null, null);
////                        SecurityContextHolder.getContext().setAuthentication(authentication);
////                    } else {
////                        LOGGER.info("Token expired.");
////                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
////                        response.setContentType("application/json");
////                        response.getWriter().write(createErrorResponse());
////                        return;
////                    }
////                } else {
////                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
////                    response.setContentType("application/json");
////                    response.getWriter().write(createErrorResponse());
////                    return;
////                }
////            } else {
////                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
////                response.setContentType("application/json");
////                response.getWriter().write(createErrorResponse());
////                return;
////            }
//                    response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
//
//            response.setHeader("Access-Control-Allow-Origin", "*");
//            filterChain.doFilter(request, response);
//        } else {
//            response.setHeader("Access-Control-Allow-Origin", "*");
//            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
//            response.setHeader("Access-Control-Max-Age", "3600");
//            response.setHeader("Access-Control-Allow-Headers", "authorization, content-type, xsrf-token");
//            response.addHeader("Access-Control-Expose-Headers", "xsrf-token");
//
//            response.setStatus(HttpServletResponse.SC_OK);
//        }
    }
//    private DecodedJWT validateToken(String token) {
//        LOGGER.info("Validating token");
//        try {
//            final Algorithm algorithm = Algorithm.HMAC256(getSecretKey());
//
//            return JWT.require(algorithm)
//                    .withIssuer("developer")
//                    .withAudience("login")
//                    .build()
//                    .verify(token);
//        }  catch (JWTVerificationException e) {
//            LOGGER.trace("Invalid Toke: {}", e.getMessage());
//            return null;
//        } catch (Exception e) {
//            LOGGER.trace("Invalid token: {}", e.getMessage());
//            return null;
//        }
//    }

//    private boolean validateTokenTime(Date tokenDate) {
//        final Date currentDate = new Date();
//        final Date tokenDatePlus60Seconds = Date.from(tokenDate.toInstant().plusSeconds(Long.parseLong(getTokenVadility())));
//        return currentDate.before(tokenDatePlus60Seconds);
//    }
//
//    private String getSecretKey() {
//        return this.projectProperties.getProperty("front-end.secret-key-token");
//    }
//
//    private String getTokenVadility() {
//        return this.projectProperties.getProperty("front-end.max-seconds-token");
//    }
//
//    private String createErrorResponse() {
//        return String.format(
//                "{\"status\": \"%s\", \"timestamp\": \"%s\", \"message\": \"%s\"}",
//                "ERROR",
//                java.time.LocalDateTime.now().toString(),
//                "Invalid token"
//        );
//    }

}