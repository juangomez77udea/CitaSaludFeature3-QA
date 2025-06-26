package com.citasalud.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-milliseconds}")
    private long jwtExpirationDate;

    // 1. Método para generar el token JWT (VERSIÓN MODERNA)
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();

        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        // Se usa un objeto SecretKey en lugar de Key
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));

        return Jwts.builder()
                .subject(username) // El método moderno es .subject()
                .issuedAt(currentDate) // .issuedAt()
                .expiration(expireDate) // .expiration()
                .signWith(key) // .signWith() ahora solo necesita la clave
                .compact();
    }

    // El método key() ya no es necesario, la clave se crea y usa directamente.
    // Si quieres mantenerlo por claridad, debería devolver SecretKey.
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }


    // 2. Método para obtener el username (email) del token JWT (VERSIÓN MODERNA)
    public String getUsername(String token) {
        Claims claims = Jwts.parser() // El método moderno es .parser()
                .verifyWith(getSigningKey()) // Se usa .verifyWith() para la clave
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    // 3. Método para validar el token JWT (VERSIÓN MODERNA)
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            // Es buena práctica loguear la excepción
            // log.error("JWT validation error: {}", e.getMessage());
            System.out.println("JWT inválido: " + e.getMessage());
        }
        return false;
    }
}