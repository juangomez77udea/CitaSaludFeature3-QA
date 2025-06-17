package com.citasalud.backend.service;

import com.citasalud.backend.domain.Especialidad; // Para cargar Especialidad
import com.citasalud.backend.domain.Medico;
import com.citasalud.backend.domain.Rol; // Para cargar Rol
import com.citasalud.backend.dto.MedicoDTO;
import com.citasalud.backend.dto.MedicoFranjasDTO;
import com.citasalud.backend.dto.MedicoResponseDTO; // Importa el nuevo DTO
import com.citasalud.backend.mapper.DisponibilidadMapper;
import com.citasalud.backend.mapper.MedicoMapper;
import com.citasalud.backend.repository.EspecialidadRepository; // Necesitas este
import com.citasalud.backend.repository.MedicoRepository;
import com.citasalud.backend.repository.RolRepository; // Necesitas este
import jakarta.transaction.Transactional; // Para actualizar/eliminar

import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicoServiceImpl implements MedicoService {
    private final MedicoRepository medicoRepository;
    private final EspecialidadRepository especialidadRepository; // Inyectar
    private final RolRepository rolRepository; // Inyectar

    public MedicoServiceImpl(MedicoRepository medicoRepository,
                             EspecialidadRepository especialidadRepository,
                             RolRepository rolRepository) {
        this.medicoRepository = medicoRepository;
        this.especialidadRepository = especialidadRepository;
        this.rolRepository = rolRepository;
    }


    @Override
    public List<MedicoResponseDTO> obtenerTodosHateoas() {
        return medicoRepository.findAll()
                .stream()
                .map(MedicoMapper::toResponseDTO) // Usar el nuevo mapper para MedicoResponseDTO
                .collect(Collectors.toList());
    }

    @Override
    public MedicoResponseDTO obtenerPorIdHateoas(Long id) {
        return medicoRepository.findById(id)
                .map(MedicoMapper::toResponseDTO)
                .orElse(null); // O lanzar una excepción NotFound
    }

    @Override
    @Transactional // Asegura que las operaciones de DB se manejen en una transacción
    public void crearMedico(MedicoDTO medicoDTO) {
        Medico medico = MedicoMapper.toEntity(medicoDTO);
        // Cargar Especialidad y Rol desde la BD, NO crear nuevos "dummy"
        if (medicoDTO.getEspecialidadId() != null) {
            Especialidad especialidad = especialidadRepository.findById(medicoDTO.getEspecialidadId())
                    .orElseThrow(() -> new RuntimeException("Especialidad no encontrada con ID: " + medicoDTO.getEspecialidadId()));
            medico.setEspecialidad(especialidad);
        }
        if (medicoDTO.getRolId() != null) {
            Rol rol = rolRepository.findById(medicoDTO.getRolId())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + medicoDTO.getRolId()));
            medico.setRolId(rol);
        }

        medicoRepository.save(medico);
    }

    @Override
    @Transactional
    public MedicoResponseDTO crearMedicoHateoas(MedicoDTO medicoDTO) {
        Medico medico = MedicoMapper.toEntity(medicoDTO);
        // Cargar Especialidad y Rol desde la BD
        if (medicoDTO.getEspecialidadId() != null) {
            Especialidad especialidad = especialidadRepository.findById(medicoDTO.getEspecialidadId())
                    .orElseThrow(() -> new RuntimeException("Especialidad no encontrada con ID: " + medicoDTO.getEspecialidadId()));
            medico.setEspecialidad(especialidad);
        }
        if (medicoDTO.getRolId() != null) {
            Rol rol = rolRepository.findById(medicoDTO.getRolId())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + medicoDTO.getRolId()));
            medico.setRolId(rol);
        }

        Medico savedMedico = medicoRepository.save(medico);
        return MedicoMapper.toResponseDTO(savedMedico); // Retorna el DTO con el ID generado
    }

    @Override
    @Transactional
    public MedicoResponseDTO actualizarMedicoHateoas(Long id, MedicoDTO medicoDTO) {
        Medico medicoExistente = medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado con ID: " + id));

        // Actualiza los campos necesarios. No actualices el password aquí a menos que sea un endpoint específico.
        medicoExistente.setNombre(medicoDTO.getNombre());
        medicoExistente.setApellido(medicoDTO.getApellido());
        medicoExistente.setEmail(medicoDTO.getEmail()); // Considera si el email puede cambiar
        medicoExistente.setTipoDocumento(medicoDTO.getTipoDocumento());
        medicoExistente.setNumeroDocumento(medicoDTO.getNumeroDocumento());

        // Actualizar Especialidad y Rol si se proporcionan nuevos IDs
        if (medicoDTO.getEspecialidadId() != null && medicoDTO.getEspecialidadId() != medicoExistente.getEspecialidad().getEspecialidadId()) {
            Especialidad especialidad = especialidadRepository.findById(medicoDTO.getEspecialidadId())
                    .orElseThrow(() -> new RuntimeException("Especialidad no encontrada con ID: " + medicoDTO.getEspecialidadId()));
            medicoExistente.setEspecialidad(especialidad);
        }
        if (medicoDTO.getRolId() != null && medicoDTO.getRolId() != medicoExistente.getRolId().getRolId()) {
            Rol rol = rolRepository.findById(medicoDTO.getRolId())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + medicoDTO.getRolId()));
            medicoExistente.setRolId(rol);
        }
        // No actualices la contraseña aquí a menos que haya un endpoint PUT /updatePassword
        // medicoExistente.setPassword(new BCryptPasswordEncoder().encode(medicoDTO.getPassword()));

        Medico updatedMedico = medicoRepository.save(medicoExistente);
        return MedicoMapper.toResponseDTO(updatedMedico);
    }

    @Override
    @Transactional
    public void eliminarMedico(Long id) {
        if (!medicoRepository.existsById(id)) {
            throw new RuntimeException("Médico no encontrado con ID: " + id);
        }
        medicoRepository.deleteById(id);
    }

    @Override
    public List<MedicoFranjasDTO> listarMedicosConFranjas() {
        List<Medico> medicos = medicoRepository.findAllWithDisponibilidades();

        return medicos.stream()
                .map(medico -> {
            MedicoFranjasDTO dto = new MedicoFranjasDTO();
            dto.setId(medico.getId());
            dto.setNombre(medico.getNombre());
            dto.setApellido(medico.getApellido());
            dto.setEmail(medico.getEmail());
            dto.setTipoDocumento(medico.getTipoDocumento());
            dto.setNumeroDocumento(medico.getNumeroDocumento());

            if (medico.getEspecialidad() != null) {
                dto.setEspecialidadId(medico.getEspecialidad().getEspecialidadId());
                dto.setEspecialidadNombre(medico.getEspecialidad().getEspecialidad());
            }
            if (medico.getRolId() != null) {
                dto.setRolId(medico.getRolId().getRolId());
                dto.setRolNombre(medico.getRolId().getNombre());
            }

            if (medico.getDisponibilidades() != null) {
                dto.setFranjasDisponibles(medico.getDisponibilidades().stream()
                                .map(DisponibilidadMapper::toDTO)
                                .collect(Collectors.toList()));
            } else {
                dto.setFranjasDisponibles(Collections.emptyList());
            }
            return dto;
        })
                .collect(Collectors.toList());
    }
}

