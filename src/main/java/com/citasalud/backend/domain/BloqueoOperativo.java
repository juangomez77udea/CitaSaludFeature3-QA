package com.citasalud.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
public class BloqueoOperativo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bloqueo_id")
    private Long bloqueoOperativoId;

    @Column(nullable = false)
    private String tipo;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(nullable = false)
    private String motivo;

    @ManyToOne
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;


}
