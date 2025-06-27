package com.citasalud.backend.mapper;

import com.citasalud.backend.domain.Disponibilidad;
import com.citasalud.backend.dto.DisponibilidadDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class DisponibilidadMapperTest {

    @Test
    void toDTO_cuandoEntidadEsValida_debeMapearCorrectamente() {
        // GIVEN: Una entidad Disponibilidad con todos sus campos
        Disponibilidad entidad = new Disponibilidad();
        entidad.setDisponibilidadId(1L);
        entidad.setDias(List.of("LUNES", "MARTES"));
        entidad.setFechaInicio(LocalDate.of(2024, 1, 1));
        entidad.setFechaFin(LocalDate.of(2024, 12, 31));
        entidad.setHoraInicio(LocalTime.of(9, 0));
        entidad.setHoraFin(LocalTime.of(17, 0));

        // WHEN: Llamamos al método de mapeo
        DisponibilidadDTO dto = DisponibilidadMapper.toDTO(entidad);

        // THEN: Verificamos que todos los campos del DTO son correctos
        assertNotNull(dto);
        assertEquals(1L, dto.getDisponibilidadId());
        assertEquals(2, dto.getDias().size());
        assertEquals("LUNES", dto.getDias().get(0));
        assertEquals(LocalDate.of(2024, 1, 1), dto.getFechaInicio());
        assertEquals(LocalDate.of(2024, 12, 31), dto.getFechaFin());
        assertEquals(LocalTime.of(9, 0), dto.getHoraInicio());
        assertEquals(LocalTime.of(17, 0), dto.getHoraFin());
    }

    @Test
    void toEntity_cuandoDtoEsValido_debeMapearCorrectamente() {
        // GIVEN: Un DTO con todos sus campos
        DisponibilidadDTO dto = new DisponibilidadDTO();
        dto.setDisponibilidadId(1L); // El ID puede ser nulo en creación, pero lo incluimos para probar el mapeo
        dto.setDias(List.of("MIERCOLES"));
        dto.setFechaInicio(LocalDate.of(2025, 2, 10));
        dto.setFechaFin(LocalDate.of(2025, 5, 10));
        dto.setHoraInicio(LocalTime.of(10, 30));
        dto.setHoraFin(LocalTime.of(12, 30));

        // WHEN: Llamamos al método de mapeo
        Disponibilidad entidad = DisponibilidadMapper.toEntity(dto);

        // THEN: Verificamos que todos los campos de la entidad son correctos
        assertNotNull(entidad);
        // El ID no se mapea en tu implementación actual, lo cual es correcto para creación.
        assertNull(entidad.getDisponibilidadId());
        assertEquals(1, entidad.getDias().size());
        assertEquals("MIERCOLES", entidad.getDias().get(0));
        assertEquals(LocalDate.of(2025, 2, 10), entidad.getFechaInicio());
        assertEquals(LocalDate.of(2025, 5, 10), entidad.getFechaFin());
        assertEquals(LocalTime.of(10, 30), entidad.getHoraInicio());
        assertEquals(LocalTime.of(12, 30), entidad.getHoraFin());
    }
}