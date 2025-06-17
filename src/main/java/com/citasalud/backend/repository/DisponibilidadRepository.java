package com.citasalud.backend.repository;

import com.citasalud.backend.domain.Disponibilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {
}