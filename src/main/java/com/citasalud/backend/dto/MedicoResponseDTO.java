package com.citasalud.backend.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel; // Importa esto

@NoArgsConstructor
@Getter
@Setter
// MedicoResponseDTO extiende RepresentationModel para poder añadir enlaces HATEOAS
public class MedicoResponseDTO extends RepresentationModel<MedicoResponseDTO> {
    private Long id; // El ID del médico es crucial para construir enlaces
    private String nombre;
    private String apellido;
    private String email;
    private String tipoDocumento;
    private String numeroDocumento;
    private Long especialidadId;
    private String especialidadNombre; // Para mostrar el nombre de la especialidad
    private Long rolId;
    private String rolNombre; // Para mostrar el nombre del rol

    // Constructor para mapeo fácil desde Medico o MedicoMapper
    public MedicoResponseDTO(Long id, String nombre, String apellido, String email,
                             String tipoDocumento, String numeroDocumento,
                             Long especialidadId, String especialidadNombre,
                             Long rolId, String rolNombre) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.especialidadId = especialidadId;
        this.especialidadNombre = especialidadNombre;
        this.rolId = rolId;
        this.rolNombre = rolNombre;
    }
}
