package com.citasalud.backend.domain;

import jakarta.persistence.*;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity

public class Rol{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rol_id")
    private Long rolId;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private String permisos;


}
