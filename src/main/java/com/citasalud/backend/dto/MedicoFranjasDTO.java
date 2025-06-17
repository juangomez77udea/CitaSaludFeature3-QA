package com.citasalud.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MedicoFranjasDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String tipoDocumento;
    private String numeroDocumento;
    private Long especialidadId;
    private String especialidadNombre;
    private Long rolId;
    private String rolNombre;

    // *** La lista de franjas asociadas ***
    private List<DisponibilidadDTO> franjasDisponibles;
}