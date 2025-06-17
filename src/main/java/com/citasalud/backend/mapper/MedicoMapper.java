package com.citasalud.backend.mapper;

import com.citasalud.backend.domain.Especialidad;
import com.citasalud.backend.domain.Medico;
import com.citasalud.backend.domain.Rol;
import com.citasalud.backend.dto.MedicoDTO; // DTO para entrada (request)
import com.citasalud.backend.dto.MedicoResponseDTO; // DTO para salida (response con HATEOAS)
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class MedicoMapper {

    // Método para convertir de Entidad Medico a MedicoResponseDTO (para respuestas con HATEOAS)
    public static MedicoResponseDTO toResponseDTO(Medico medico) {
        if (medico == null) {
            return null;
        }
        MedicoResponseDTO dto = new MedicoResponseDTO();
        dto.setId(medico.getId()); // Asegúrate de mapear el ID de la entidad
        dto.setNombre(medico.getNombre());
        dto.setApellido(medico.getApellido());
        dto.setEmail(medico.getEmail());
        dto.setTipoDocumento(medico.getTipoDocumento());
        dto.setNumeroDocumento(medico.getNumeroDocumento());

        if (medico.getEspecialidad() != null) {
            dto.setEspecialidadId(medico.getEspecialidad().getEspecialidadId());
            dto.setEspecialidadNombre(medico.getEspecialidad().getEspecialidad()); // Asumiendo que Especialidad tiene getEspecialidad()
        }
        if (medico.getRolId() != null) {
            dto.setRolId(medico.getRolId().getRolId());
            dto.setRolNombre(medico.getRolId().getNombre()); // Asumiendo que Rol tiene getNombre()
        }
        return dto;
    }

    // Método para convertir de MedicoDTO (request) a Entidad Medico
    public static Medico toEntity(MedicoDTO dto) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (dto == null) {
            return null;
        }
        Medico medico = new Medico();
        // El ID no se establece aquí, se genera por la DB o se usa en actualizaciones
        medico.setNombre(dto.getNombre());
        medico.setApellido(dto.getApellido());
        medico.setEmail(dto.getEmail());
        medico.setPassword(encoder.encode(dto.getPassword()));
        medico.setTipoDocumento(dto.getTipoDocumento());
        medico.setNumeroDocumento(dto.getNumeroDocumento());

        // Importante: No se debe crear nuevas entidades de Especialidad y Rol aquí directamente.
        // Debes cargar las entidades existentes de la base de datos usando sus respectivos repositorios.
        // Los "DUMMY"  deben ser reemplazados por la inyección de los repositorios de Especialidad y Rol en el servicio,
        // y luego buscando la entidad por ID antes de asignarla al médico.
        if (dto.getEspecialidadId() != null) {
            Especialidad especialidad = new Especialidad(); // Esto DEBE SER cargado de la DB en el Servicio
            especialidad.setEspecialidadId(dto.getEspecialidadId());
            medico.setEspecialidad(especialidad);
        }
        if (dto.getRolId() != null) {
            Rol rol = new Rol(); // Esto DEBE SER cargado de la DB en el Servicio
            rol.setRolId(dto.getRolId());
            medico.setRolId(rol);
        }
        return medico;
    }
}
