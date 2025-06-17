package com.citasalud.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "medico")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Medico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medico_id")
    private Long id;
    private String nombre;
    private String apellido;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "especialidad_id", nullable = false)
    private Especialidad especialidad;

    @ManyToOne
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rolId;

    @Column(nullable = false)
    private String tipoDocumento;

    @Column(nullable = false, unique = true)
    private String numeroDocumento;

    @OneToMany(mappedBy = "medico", cascade = CascadeType.ALL, orphanRemoval = true) // CascadeType.ALL y orphanRemoval son comunes para dependientes
    private List<Disponibilidad> disponibilidades;

    // Y sus getters/setters:


}

