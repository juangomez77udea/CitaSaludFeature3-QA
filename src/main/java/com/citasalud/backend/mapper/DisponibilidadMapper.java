package com.citasalud.backend.mapper;

import com.citasalud.backend.domain.Disponibilidad;
import com.citasalud.backend.dto.DisponibilidadDTO;

public class DisponibilidadMapper {
    public static DisponibilidadDTO toDTO(Disponibilidad entity) {
        DisponibilidadDTO dto = new DisponibilidadDTO();
        dto.setDisponibilidadId(entity.getDisponibilidadId()); // Añade esta línea
        dto.setDias(entity.getDias());
        dto.setFechaInicio(entity.getFechaInicio());
        dto.setFechaFin(entity.getFechaFin());
        dto.setHoraInicio(entity.getHoraInicio());
        dto.setHoraFin(entity.getHoraFin()); 
        return dto;
    }

    public static Disponibilidad toEntity(DisponibilidadDTO dto) {
        Disponibilidad entity = new Disponibilidad();
        // Si el DTO trae ID (para actualizaciones), se puede mapear aquí.
        // Pero para la creación, generalmente la DB lo genera.
        // entity.setDisponibilidadId(dto.getDisponibilidadId()); // Solo si es una actualización y el ID se envía en el DTO
        entity.setDias(dto.getDias());
        entity.setFechaInicio(dto.getFechaInicio());
        entity.setFechaFin(dto.getFechaFin());
        entity.setHoraInicio(dto.getHoraInicio());
        entity.setHoraFin(dto.getHoraFin());
        return entity;
    }
}
