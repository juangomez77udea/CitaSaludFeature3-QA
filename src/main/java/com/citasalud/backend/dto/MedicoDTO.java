package com.citasalud.backend.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MedicoDTO {

    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private String tipoDocumento;
    private String numeroDocumento;
    // Para las relaciones, a menudo se incluye solo el ID o un DTO simple
    private Long especialidadId;
     // O podrías incluir el nombre si es útil en el DTO
    private Long rolId;
     // O podrías incluir el nombre si es útil en el DTO

    // Aquí Lombok se encarga de getters, setters, y constructores

    // Nota: El password NO se incluye en el DTO por seguridad.
}



