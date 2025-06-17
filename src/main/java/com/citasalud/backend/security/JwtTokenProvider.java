package com.citasalud.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component // Para que Spring lo detecte como un componente e inyecte
public class JwtTokenProvider {

    // Secreto para firmar el JWT. ¡DEBE SER SEGURO y NO EXPONERSE EN CÓDIGO!
    // Lo cargaremos desde application.properties o variables de entorno.
    @Value("${app.jwt-secret}")
    private String jwtSecret;

    // Tiempo de expiración del JWT en milisegundos (ej. 1 hora = 3600000 ms)
    @Value("${app.jwt-expiration-milliseconds}")
    private long jwtExpirationDate;

    // 1. Metodo para generar el token JWT
    public String generateToken(Authentication authentication) {
        String username = authentication.getName(); // El email del usuario

        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        String token = Jwts.builder()
                .setSubject(username) // El sujeto del token (ej. email del usuario)
                .setIssuedAt(new Date()) // Fecha de emisión
                .setExpiration(expireDate) // Fecha de expiración
                .signWith(key(), SignatureAlgorithm.HS512) // Firma el token con la clave secreta y algoritmo
                .compact(); // Construye y compacta el token
        return token;
    }

    // 2. Método para obtener la clave secreta decodificada
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // 3. Método para obtener el username (email) del token JWT
    public String getUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // 4. Método para validar el token JWT
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token);
            return true; // Token válido
        } catch (Exception e) {
            // Aquí puedes añadir logs más específicos para diferentes excepciones
            // de JWT (SignatureException, ExpiredJwtException, MalformedJwtException, etc.)
            System.out.println("JWT inválido: " + e.getMessage());
        }
        return false; // Token inválido
    }
}
