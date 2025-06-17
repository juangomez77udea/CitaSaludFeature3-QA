package com.citasalud.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.hateoas.RepresentationModel; // Importa esto

@NoArgsConstructor
@AllArgsConstructor
@Data
// DisponibilidadDTO extiende RepresentationModel para poder añadir enlaces HATEOAS
public class DisponibilidadDTO extends RepresentationModel<DisponibilidadDTO> {
    // Necesitamos el ID de la disponibilidad para construir los enlaces
    private Long disponibilidadId;

    @NotNull(message = "La lista de días no puede ser nula")
    @Size(min = 1, message = "Debe seleccionar al menos un día")
    private List<String> dias;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    @NotNull(message = "La hora de inicio no puede ser nula")
    private LocalTime horaInicio;
    @NotNull(message = "La hora de fin no puede ser nula")
    private LocalTime horaFin;

}




