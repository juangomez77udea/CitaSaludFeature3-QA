package com.citasalud.backend.dto.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String adminPassword = "admin1234";
        String medicoPassword = "medico1234";

        String encodedAdminPassword = encoder.encode(adminPassword);
        String encodedMedicoPassword = encoder.encode(medicoPassword);

        System.out.println("--- Copia y pega estas líneas EXACTAS en tu import.sql ---");
        System.out.println(); // Línea en blanco para separar

        // Sentencia para el Administrador
        String adminSql = String.format(
                "INSERT INTO medico (medico_id, nombre, apellido, email, password, tipo_documento, numero_documento, especialidad_id, rol_id) " +
                        "VALUES (99, 'Admin', 'Principal', 'admin@citasalud.com', '%s', 'NIT', '999999999', 1, 1);",
                encodedAdminPassword
        );
        System.out.println("-- Admin (contraseña: admin1234)");
        System.out.println(adminSql);
        System.out.println(); // Línea en blanco

        // Sentencia para el Médico
        String medicoSql = String.format(
                "INSERT INTO medico (medico_id, nombre, apellido, email, password, tipo_documento, numero_documento, especialidad_id, rol_id) " +
                        "VALUES (100, 'Carlos', 'Santana', 'carlos.santana@example.com', '%s', 'CC', '12345678', 1, 3);",
                encodedMedicoPassword
        );
        System.out.println("-- Medico (contraseña: medico1234)");
        System.out.println(medicoSql);
    }
}