package com.citasalud.backend.service;

import com.citasalud.backend.dto.MedicoDTO;
import com.citasalud.backend.dto.MedicoFranjasDTO;
import com.citasalud.backend.dto.MedicoResponseDTO; // Importa el nuevo DTO

import java.util.List;

public interface MedicoService {
    // Mantener este si todavía lo necesitas sin HATEOAS

    // Nuevo método para obtener todos los médicos con HATEOAS (retorna MedicoResponseDTO)
    List<MedicoResponseDTO> obtenerTodosHateoas();

    // Nuevo método para obtener un médico por ID con HATEOAS
    MedicoResponseDTO obtenerPorIdHateoas(Long id);

    void crearMedico(MedicoDTO medicoDTO);
    // Nuevo método para crear médico que retorna el DTO para HATEOAS
    MedicoResponseDTO crearMedicoHateoas(MedicoDTO medicoDTO);

    // Nuevo método para actualizar médico que retorna el DTO para HATEOAS
    MedicoResponseDTO actualizarMedicoHateoas(Long id, MedicoDTO medicoDTO);

    void eliminarMedico(Long id); // Para eliminar no se necesita DTO de retorno.

    List<MedicoFranjasDTO> listarMedicosConFranjas();
}
