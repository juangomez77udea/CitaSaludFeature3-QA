package com.citasalud.backend.service;

import com.citasalud.backend.dto.DisponibilidadDTO;

import java.util.List;

public interface DisponibilidadService {
    void agregarFranja(DisponibilidadDTO dto, Long medicoId);
    // Nuevo método para agregar franja que retorna el DTO con HATEOAS
    DisponibilidadDTO agregarFranjaHateoas(DisponibilidadDTO dto, Long medicoId);

    List<DisponibilidadDTO> obtenerDisponibilidadesPorMedico(Long medicoId);

    List<DisponibilidadDTO> listarFranjas();
    // Nuevo método para obtener una franja por ID con HATEOAS
    DisponibilidadDTO obtenerFranjaPorIdHateoas(Long id);

    void eliminarFranja(Long franjaId);
    DisponibilidadDTO actualizarFranja(Long franjaId, DisponibilidadDTO dto);
    // Nuevo método para actualizar franja que retorna el DTO con HATEOAS
    DisponibilidadDTO actualizarFranjaHateoas(Long franjaId, DisponibilidadDTO dto);
}

