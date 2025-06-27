package com.citasalud.backend.mapper;

import com.citasalud.backend.DataProvider;
import com.citasalud.backend.domain.Medico;
import com.citasalud.backend.dto.MedicoDTO;
import com.citasalud.backend.dto.MedicoResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class MedicoMapperTest {

    @Test
    void toResponseDTO_cuandoEntidadEsCompleta_debeMapearTodosLosCampos() {
        // GIVEN: Una entidad Medico con todas sus relaciones
        Medico medico = DataProvider.crearListaMedicosConRelaciones().get(0);

        // WHEN: Llamamos al método de mapeo
        MedicoResponseDTO dto = MedicoMapper.toResponseDTO(medico);

        // THEN: Verificamos todos los campos, incluyendo los de las relaciones
        assertNotNull(dto);
        assertEquals(medico.getId(), dto.getId());
        assertEquals(medico.getNombre(), dto.getNombre());
        assertEquals(medico.getApellido(), dto.getApellido());
        assertEquals(medico.getEmail(), dto.getEmail());
        assertEquals(medico.getEspecialidad().getEspecialidadId(), dto.getEspecialidadId());
        // --- LÍNEA CORREGIDA ---
        assertEquals(medico.getEspecialidad().getEspecialidad(), dto.getEspecialidadNombre());
        assertEquals(medico.getRolId().getRolId(), dto.getRolId());
        assertEquals(medico.getRolId().getNombre(), dto.getRolNombre());
    }

    @Test
    void toResponseDTO_cuandoRelacionesSonNulas_debeManejarloCorrectamente() {
        // GIVEN: Una entidad Medico sin especialidad ni rol
        Medico medico = DataProvider.crearListaMedicosEntidad().get(0);
        medico.setEspecialidad(null);
        medico.setRolId(null);

        // WHEN
        MedicoResponseDTO dto = MedicoMapper.toResponseDTO(medico);

        // THEN: Verificamos que los campos de relaciones son nulos en el DTO
        assertNotNull(dto);
        assertEquals(medico.getId(), dto.getId());
        assertNull(dto.getEspecialidadId());
        assertNull(dto.getEspecialidadNombre());
        assertNull(dto.getRolId());
        assertNull(dto.getRolNombre());
    }

    @Test
    void toResponseDTO_cuandoEntidadEsNula_debeRetornarNull() {
        // WHEN & THEN
        assertNull(MedicoMapper.toResponseDTO(null));
    }

    @Test
    void toEntity_cuandoDtoEsValido_debeMapearCamposYCrearRelaciones() {
        // GIVEN: Un DTO de entrada
        MedicoDTO dto = DataProvider.crearMedicoDTO();

        // WHEN
        Medico entidad = MedicoMapper.toEntity(dto);

        // THEN: Verificamos que los campos básicos se mapearon
        assertNotNull(entidad);
        assertEquals(dto.getNombre(), entidad.getNombre());
        assertEquals(dto.getApellido(), entidad.getApellido());
        assertEquals(dto.getEmail(), entidad.getEmail());

        // Verificamos que la contraseña fue encriptada
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        assertTrue(encoder.matches(dto.getPassword(), entidad.getPassword()));

        // Verificamos que se crearon los objetos de relación con sus IDs
        assertNotNull(entidad.getEspecialidad());
        assertEquals(dto.getEspecialidadId(), entidad.getEspecialidad().getEspecialidadId());
        assertNotNull(entidad.getRolId());
        assertEquals(dto.getRolId(), entidad.getRolId().getRolId());
    }

    @Test
    void toEntity_cuandoIdsDeRelacionSonNulos_noDebeCrearRelaciones() {
        // GIVEN: Un DTO sin IDs de especialidad ni rol
        MedicoDTO dto = DataProvider.crearMedicoDTO();
        dto.setEspecialidadId(null);
        dto.setRolId(null);

        // WHEN
        Medico entidad = MedicoMapper.toEntity(dto);

        // THEN
        assertNotNull(entidad);
        // Verificamos que las entidades relacionadas son nulas, porque los if no se ejecutaron
        assertNull(entidad.getEspecialidad());
        assertNull(entidad.getRolId());
    }

    @Test
    void toEntity_cuandoDtoEsNulo_debeRetornarNull() {
        // WHEN & THEN
        assertNull(MedicoMapper.toEntity(null));
    }
}