package com.citasalud.backend.dto.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "contraseña1234"; // <-- ESTA ES LA CONTRASEÑA EN TEXTO PLANO PARA EL LOGIN
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("Contraseña hasheada (copia esto): " + encodedPassword);
        // Ejemplo de salida: $2a$10$abcdefghijklmnopqrstuvwxyza.abcdefghijkl.abcdefghijkl.abcdefghijkl

        rawPassword = "hola1234"; // <-- ESTA ES LA CONTRASEÑA EN TEXTO PLANO PARA EL LOGIN
        encodedPassword = encoder.encode(rawPassword);
        System.out.println("Contraseña hasheada (copia esto): " + encodedPassword);

        rawPassword = "passwordSeguro123"; // <-- ESTA ES LA CONTRASEÑA EN TEXTO PLANO PARA EL LOGIN
        encodedPassword = encoder.encode(rawPassword);
        System.out.println("Contraseña hasheada (copia esto): " + encodedPassword);
    }
}
