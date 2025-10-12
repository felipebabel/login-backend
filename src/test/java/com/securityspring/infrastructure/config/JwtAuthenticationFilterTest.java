package com.securityspring.infrastructure.config;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Collections;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.securityspring.application.service.api.JwtServiceApi;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

class JwtAuthenticationFilterTest {

    @Mock
    private JwtServiceApi jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @Captor
    private ArgumentCaptor<Integer> statusCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @Test
    void testOptionsMethod() throws ServletException, IOException {
        when(request.getMethod()).thenReturn("OPTIONS");

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void testLoginPathSkipped() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/v1/login");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testAuthHeaderMissing() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/v1/other");
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testExpiredJwtToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer token123");
        when(jwtService.extractUsername("token123")).thenThrow(new ExpiredJwtException(null, null, "Token expired"));
        when(request.getRequestURI()).thenReturn("/api/v1/other");

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setHeader(eq("Token-Expired"), eq("true"));
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void testInvalidJwtToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer token123");
        when(jwtService.extractUsername("token123")).thenThrow(new JwtException("Invalid"));
        when(request.getRequestURI()).thenReturn("/api/v1/other");

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setHeader(eq("WWW-Authenticate"), contains("invalid_token"));
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void testValidJwtTokenWithoutRole() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer token123");
        when(request.getRequestURI()).thenReturn("/api/v1/other");
        when(jwtService.extractUsername("token123")).thenReturn("user1");
        when(userDetailsService.loadUserByUsername("user1")).thenReturn(userDetails);
        when(jwtService.isTokenValid("token123", userDetails)).thenReturn(true);
        when(jwtService.extractClaimByName("token123", "role")).thenReturn(null);
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testValidJwtTokenWithRole() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer token123");
        when(request.getRequestURI()).thenReturn("/api/v1/other");
        when(jwtService.extractUsername("token123")).thenReturn("user1");
        when(userDetailsService.loadUserByUsername("user1")).thenReturn(userDetails);
        when(jwtService.isTokenValid("token123", userDetails)).thenReturn(true);
        when(jwtService.extractClaimByName("token123", "role")).thenReturn("ADMIN");
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());

        filter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(userDetails, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(filterChain).doFilter(request, response);
    }


}