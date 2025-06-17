package com.citasalud.backend.controller;

import com.citasalud.backend.dto.auth.AuthResponse;
import com.citasalud.backend.dto.auth.LoginRequest;
import com.citasalud.backend.security.JwtTokenProvider; // Esta clase la crearemos en el siguiente paso



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails; // Importar UserDetails
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/auth") // Ruta base para los endpoints de autenticación

public class AuthController {

    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider; // Inyectaremos el proveedor de JWT

    // Constructor para inyección de dependencias
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica al usuario con email y contraseña. Retorna un token JWT si las credenciales son válidas."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Login no válido",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            // Autentica al usuario usando el AuthenticationManager
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            // Si la autenticación es exitosa, establece la autenticación en el contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Genera el JWT
            String token = jwtTokenProvider.generateToken(authentication);

            // Obtén los detalles del usuario autenticado para obtener el rol
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String userEmail = userDetails.getUsername();
            String userRole = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst() // Asumimos un solo rol principal por simplicidad
                    .orElse("UNKNOWN"); // O un valor por defecto

            // Construye la respuesta de autenticación
            AuthResponse authResponse = AuthResponse.builder()
                    .jwt(token)
                    .email(userEmail)
                    .role(userRole)
                    .build();

            return new ResponseEntity<>(authResponse, HttpStatus.OK);

        } catch (Exception e) {
            // Si la autenticación falla (ej. credenciales inválidas)
            // Criterio de aceptación: "Login no válido" en rojo
            return new ResponseEntity<>("Login no válido", HttpStatus.UNAUTHORIZED);
        }
    }
}
