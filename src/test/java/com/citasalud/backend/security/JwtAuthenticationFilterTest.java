package com.citasalud.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;


import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_conTokenValido_debeAutenticarUsuario() throws ServletException, IOException {
        // GIVEN
        String token = "valid-token";
        String username = "user@example.com";
        UserDetails userDetails = new User(username, "", Collections.emptyList());

        request.setRequestURI("/api/protected-resource");
        request.addHeader("Authorization", "Bearer " + token);

        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getUsername(token)).thenReturn(username);
        // Ahora el mock es del tipo correcto, así que esto funcionará
        when(customUserDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        // WHEN
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // THEN
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(username, SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain).doFilter(request, response);
    }

    // --- Tests de cobertura de ramas ---

    @Test
    void doFilterInternal_sinCabeceraAuthorization_debeContinuarSinAutenticar() throws ServletException, IOException {
        request.setRequestURI("/api/protected-resource");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtTokenProvider);
    }

    @Test
    void doFilterInternal_conCabeceraSinBearerPrefix_debeContinuarSinAutenticar() throws ServletException, IOException {
        request.setRequestURI("/api/protected-resource");
        request.addHeader("Authorization", "token-sin-bearer");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_conTokenInvalido_debeContinuarSinAutenticar() throws ServletException, IOException {
        String invalidToken = "invalid-token";
        request.setRequestURI("/api/protected-resource");
        request.addHeader("Authorization", "Bearer " + invalidToken);
        when(jwtTokenProvider.validateToken(invalidToken)).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verify(customUserDetailsService, never()).loadUserByUsername(anyString());
    }
}