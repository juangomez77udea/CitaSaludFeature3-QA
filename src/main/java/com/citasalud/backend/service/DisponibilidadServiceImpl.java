package com.citasalud.backend.service;

// package com.citasalud.backend.service;

import com.citasalud.backend.domain.Disponibilidad;
import com.citasalud.backend.domain.Medico;
import com.citasalud.backend.dto.DisponibilidadDTO;
import com.citasalud.backend.mapper.DisponibilidadMapper;
import com.citasalud.backend.repository.DisponibilidadRepository;
import com.citasalud.backend.repository.MedicoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DisponibilidadServiceImpl implements DisponibilidadService {
    private final DisponibilidadRepository franjaRepo;
    private final MedicoRepository medicoRepo;

    public DisponibilidadServiceImpl(DisponibilidadRepository franjaRepo, MedicoRepository medicoRepo) {
        this.franjaRepo = franjaRepo;
        this.medicoRepo = medicoRepo;
    }

    @Override
    @Transactional
    public void agregarFranja(DisponibilidadDTO dto, Long medicoId) {
        Optional<Medico> medicoOpt = medicoRepo.findById(medicoId);
        if (medicoOpt.isEmpty()) {
            throw new RuntimeException("Médico no encontrado");
        }
        Medico medico = medicoOpt.get();
        Disponibilidad franja = DisponibilidadMapper.toEntity(dto);
        franja.setMedico(medico);
        franjaRepo.save(franja);
    }

    @Override
    public List<DisponibilidadDTO> obtenerDisponibilidadesPorMedico(Long medicoId) {
        // Asegúrate de que tu MedicoRepository tenga un método para encontrar disponibilidades por medico
        // O si tu entidad Disponibilidad tiene un campo `medico` con @ManyToOne, puedes usar:
        // return franjaRepo.findByMedicoId(medicoId).stream() // Necesitarías un método en DisponibilidadRepository
        //         .map(DisponibilidadMapper::toDTO)
        //         .collect(Collectors.toList());

        // Alternativa si no tienes findByMedicoId en DisponibilidadRepository:
        // Carga el médico y luego sus disponibilidades, si están eagerly fetched o usa @Transactional
        return medicoRepo.findById(medicoId)
                .map(medico -> medico.getDisponibilidades().stream()
                        .map(DisponibilidadMapper::toDTO)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList()); // Importar java.util.Collections
    }

    @Override
    @Transactional
    public DisponibilidadDTO agregarFranjaHateoas(DisponibilidadDTO dto, Long medicoId) {
        Optional<Medico> medicoOpt = medicoRepo.findById(medicoId);
        if (medicoOpt.isEmpty()) {
            throw new RuntimeException("Médico no encontrado");
        }
        Medico medico = medicoOpt.get();
        Disponibilidad franja = DisponibilidadMapper.toEntity(dto);
        franja.setMedico(medico);
        Disponibilidad savedFranja = franjaRepo.save(franja);
        return DisponibilidadMapper.toDTO(savedFranja); // Retorna el DTO con el ID generado
    }


    @Override
    public List<DisponibilidadDTO> listarFranjas() {
        return franjaRepo.findAll().stream()
                .map((Disponibilidad entity) -> DisponibilidadMapper.toDTO(entity))
                .collect(Collectors.toList());
    }

    @Override
    public DisponibilidadDTO obtenerFranjaPorIdHateoas(Long id) {
        return franjaRepo.findById(id)
                .map(DisponibilidadMapper::toDTO)
                .orElse(null); // O lanzar una excepción NotFound
    }

    @Override
    @Transactional
    public void eliminarFranja(Long franjaId) {
        if (!franjaRepo.existsById(franjaId)) {
            throw new RuntimeException("Franja horaria con ID " + franjaId + " no encontrada");
        }
        franjaRepo.deleteById(franjaId);
    }

    @Override
    @Transactional
    public DisponibilidadDTO actualizarFranja(Long franjaId, DisponibilidadDTO dto) {
        Disponibilidad franjaExistente = franjaRepo.findById(franjaId)
                .orElseThrow(() -> new RuntimeException("Franja horaria con ID " + franjaId + " no encontrada"));

        franjaExistente.setDias(dto.getDias());
        franjaExistente.setFechaInicio(dto.getFechaInicio());
        franjaExistente.setFechaFin(dto.getFechaFin());
        franjaExistente.setHoraInicio(dto.getHoraInicio());
        franjaExistente.setHoraFin(dto.getHoraFin());

        Disponibilidad franjaActualizada = franjaRepo.save(franjaExistente);
        return DisponibilidadMapper.toDTO(franjaActualizada);
    }

    @Override
    @Transactional
    public DisponibilidadDTO actualizarFranjaHateoas(Long franjaId, DisponibilidadDTO dto) {
        Disponibilidad franjaExistente = franjaRepo.findById(franjaId)
                .orElseThrow(() -> new RuntimeException("Franja horaria con ID " + franjaId + " no encontrada"));

        franjaExistente.setDias(dto.getDias());
        franjaExistente.setFechaInicio(dto.getFechaInicio());
        franjaExistente.setFechaFin(dto.getFechaFin());
        franjaExistente.setHoraInicio(dto.getHoraInicio());
        franjaExistente.setHoraFin(dto.getHoraFin());

        Disponibilidad franjaActualizada = franjaRepo.save(franjaExistente);
        return DisponibilidadMapper.toDTO(franjaActualizada);
    }
}
