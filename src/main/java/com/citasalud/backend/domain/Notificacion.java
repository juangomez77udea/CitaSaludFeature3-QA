package com.citasalud.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
public class Notificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notificacion_id")
    private Long idNotificacion;
    @Column(name = "mensaje", nullable = false)
    private String mensaje;

    @Column(name = "fecha", nullable = false)
    private LocalTime fecha;

    @ManyToOne
    @JoinColumn(name = "bloqueo_id", nullable = false)
    private BloqueoOperativo bloqueoOperativo;

}

