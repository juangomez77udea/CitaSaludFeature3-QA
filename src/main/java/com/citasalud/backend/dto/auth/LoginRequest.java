package com.citasalud.backend.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Petición de inicio de sesión que contiene las credenciales del usuario.")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginRequest {

    @Schema(description = "Correo electrónico del usuario", example = "usuario@example.com", required = true)
    private String email;
    @Schema(description = "Contraseña del usuario", example = "123456", required = true)
    private String password;
}
