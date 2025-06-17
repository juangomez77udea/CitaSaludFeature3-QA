package com.citasalud.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Disponibilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "disponibilidad_id")
    private Long disponibilidadId;

    @ElementCollection
    @CollectionTable(name = "disponibilidad_dias", joinColumns = @JoinColumn(name = "disponibilidad_id"))
    @Column(name = "dia")
    private List<String> dias; // Ej: ["LUNES", "MARTES"]

    @Column(name = "fecha_inicio", nullable = false) // Asumiendo que también hay fecha_inicio NOT NULL basado en el error
    private LocalDate fechaInicio; // Podría ser necesario si fecha_fin lo es

    @Column(name = "fecha_fin", nullable = false) // Mapea a la columna que da problema
    private LocalDate fechaFin; // Usa LocalDate para fechas

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @ManyToOne
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;
}

