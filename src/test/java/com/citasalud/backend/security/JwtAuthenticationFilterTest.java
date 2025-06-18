// Ubicación: src/test/java/com/citasalud/backend/security/JwtAuthenticationFilterTest.java
package com.citasalud.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach; // <--- Importante
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

    // --- CORRECCIÓN FINAL Y DEFINITIVA ---
    // Este método se ejecuta ANTES de CADA test y limpia el contexto de seguridad.
    // Esto garantiza que cada test comience en un estado limpio, sin importar el orden.
    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_conRutaPublica_debeSaltarValidacion() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/swagger-ui/index.html");
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtTokenProvider, customUserDetailsService);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_sinToken_debeContinuarFiltroSinAutenticar() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/medicos");
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_conTokenInvalido_debeContinuarFiltroSinAutenticar() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/medicos");
        request.addHeader("Authorization", "Bearer token-invalido");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtTokenProvider.validateToken("token-invalido")).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_conTokenValido_debeAutenticarUsuario() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/medicos");
        request.addHeader("Authorization", "Bearer token-valido");
        MockHttpServletResponse response = new MockHttpServletResponse();

        String username = "test@example.com";
        UserDetails userDetails = new User(username, "", Collections.emptyList());

        when(jwtTokenProvider.validateToken("token-valido")).thenReturn(true);
        when(jwtTokenProvider.getUsername("token-valido")).thenReturn(username);
        when(customUserDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(username, SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_conEncabezadoSinBearer_debeContinuarSinAutenticar() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/medicos");
        request.addHeader("Authorization", "token-sin-bearer");
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}