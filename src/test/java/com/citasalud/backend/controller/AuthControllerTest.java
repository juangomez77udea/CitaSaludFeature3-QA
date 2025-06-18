// Ubicación: src/test/java/com/citasalud/backend/controller/AuthControllerTest.java
package com.citasalud.backend.controller;

import com.citasalud.backend.dto.auth.AuthResponse;
import com.citasalud.backend.dto.auth.LoginRequest;
import com.citasalud.backend.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthController authController;

    @Test
    void authenticateUser_conCredencialesValidas_debeRetornarOkYToken() {
        // ARRANGE
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password");

        // 1. Simular un usuario autenticado con sus detalles
        User userDetails = new User(
                loginRequest.getEmail(),
                loginRequest.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_MEDICO"))
        );

        // 2. Crear un objeto Authentication que devoldería el AuthenticationManager
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // 3. Configurar los mocks
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("mock.jwt.token");

        // ACT
        ResponseEntity<?> responseEntity = authController.authenticateUser(loginRequest);

        // ASSERT
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        AuthResponse authResponse = (AuthResponse) responseEntity.getBody();
        assertEquals("mock.jwt.token", authResponse.getJwt());
        assertEquals("test@example.com", authResponse.getEmail());
        assertEquals("ROLE_MEDICO", authResponse.getRole());
    }

    @Test
    void authenticateUser_conCredencialesInvalidas_debeRetornarUnauthorized() {
        // ARRANGE
        LoginRequest loginRequest = new LoginRequest("wrong@example.com", "wrongpassword");

        // Configurar el mock para que lance una excepción, simulando un login fallido
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciales inválidas"));

        // ACT
        ResponseEntity<?> responseEntity = authController.authenticateUser(loginRequest);

        // ASSERT
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals("Login no válido", responseEntity.getBody());
    }
}