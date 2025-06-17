package com.citasalud.backend.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder // Anotación de Lombok para usar el patrón Builder
@Schema(description = "Respuesta devuelta tras autenticación exitosa. Contiene el JWT y datos del usuario.")
public class AuthResponse {

    @Schema(description = "Token JWT generado para autenticación", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6...")
    private String jwt;

    @Schema(description = "Correo electrónico del usuario autenticado", example = "usuario@example.com")
    private String email; // o username

    @Schema(description = "Rol asignado al usuario", example = "COORDINADOR")
    private String role; // El rol del usuario (ej. "MEDICO", "COORDINADOR")
}