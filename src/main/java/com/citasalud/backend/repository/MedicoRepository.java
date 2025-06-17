package com.citasalud.backend.repository;

import com.citasalud.backend.domain.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {
    @Query("SELECT DISTINCT m FROM Medico m LEFT JOIN FETCH m.disponibilidades")
    List<Medico> findAllWithDisponibilidades();
    Optional<Medico> findByEmail(String email);
}
