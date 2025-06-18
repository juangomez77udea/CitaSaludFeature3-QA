package com.citasalud.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        jwtTokenProvider = new JwtTokenProvider();

        // Generamos un secreto suficientemente largo para el algoritmo HS512 y lo codificamos en Base64
        String secret = "miSecretoSuperLargoYSeguroParaLasPruebasDeHS512QueNoDebeSerCorto";
        String base64Secret = Base64.getEncoder().encodeToString(secret.getBytes());

        // Usamos Reflection para "inyectar" los valores que normalmente vienen de @Value
        Field jwtSecretField = JwtTokenProvider.class.getDeclaredField("jwtSecret");
        jwtSecretField.setAccessible(true);
        ReflectionUtils.setField(jwtSecretField, jwtTokenProvider, base64Secret);

        Field jwtExpirationField = JwtTokenProvider.class.getDeclaredField("jwtExpirationDate");
        jwtExpirationField.setAccessible(true);
        ReflectionUtils.setField(jwtExpirationField, jwtTokenProvider, 60000L); // 60 segundos de expiración
    }

    @Test
    void generateToken_debeCrearUnTokenValido() {
        // ARRANGE
        String username = "test@example.com";
        when(authentication.getName()).thenReturn(username);

        // ACT
        String token = jwtTokenProvider.generateToken(authentication);

        // ASSERT
        assertNotNull(token);
        assertFalse(token.isEmpty());
        // Un JWT válido tiene dos puntos
        assertEquals(2, token.chars().filter(ch -> ch == '.').count());
    }

    @Test
    void getUsername_conTokenValido_debeRetornarUsername() {
        // ARRANGE
        String username = "test@example.com";
        when(authentication.getName()).thenReturn(username);
        String token = jwtTokenProvider.generateToken(authentication);

        // ACT
        String extractedUsername = jwtTokenProvider.getUsername(token);

        // ASSERT
        assertEquals(username, extractedUsername);
    }

    @Test
    void validateToken_conTokenValido_debeRetornarTrue() {
        // ARRANGE
        when(authentication.getName()).thenReturn("user");
        String token = jwtTokenProvider.generateToken(authentication);

        // ACT & ASSERT
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    void validateToken_conTokenInvalido_debeRetornarFalse() {
        // ARRANGE
        String invalidToken = "un.token.invalido";
        String malformedToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiaWF0IjoxNzE4NjYy"; // Token incompleto
        String emptyToken = "";

        // ACT & ASSERT
        assertFalse(jwtTokenProvider.validateToken(invalidToken));
        assertFalse(jwtTokenProvider.validateToken(malformedToken));
        assertFalse(jwtTokenProvider.validateToken(null));
        assertFalse(jwtTokenProvider.validateToken(emptyToken));
    }

    @Test
    void validateToken_conTokenExpirado_debeRetornarFalse() throws Exception {
        // ARRANGE
        // Inyectamos un tiempo de expiración negativo para que el token se genere ya expirado
        Field jwtExpirationField = JwtTokenProvider.class.getDeclaredField("jwtExpirationDate");
        jwtExpirationField.setAccessible(true);

        ReflectionUtils.setField(jwtExpirationField, jwtTokenProvider, -1000L); // 1 segundo en el pasado

        when(authentication.getName()).thenReturn("user");
        String expiredToken = jwtTokenProvider.generateToken(authentication);

        // ACT & ASSERT
        assertFalse(jwtTokenProvider.validateToken(expiredToken));
    }
}