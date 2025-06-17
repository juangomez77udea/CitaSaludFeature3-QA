package com.citasalud.backend.controller;

import com.citasalud.backend.dto.MedicoDTO; // Para requests (crear/actualizar)
import com.citasalud.backend.dto.MedicoResponseDTO; // Para responses (con HATEOAS)
import com.citasalud.backend.dto.MedicoFranjasDTO;
import com.citasalud.backend.service.MedicoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

// HATEOAS Imports
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*; // Importa estáticamente linkTo y methodOn

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/medicos")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "BearerAuth")
public class MedicoController {
    private final MedicoService medicoService;

    public MedicoController(MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/obtenermedicos")
    // Cambiar el tipo de retorno para HATEOAS
    public ResponseEntity<CollectionModel<EntityModel<MedicoResponseDTO>>> obtenerMedicos() {
        List<MedicoResponseDTO> medicos = medicoService.obtenerTodosHateoas(); // Nuevo método en servicio que retorna MedicoResponseDTO

        List<EntityModel<MedicoResponseDTO>> medicoModels = medicos.stream()
                .map(medicoDTO -> EntityModel.of(medicoDTO, // Crea un EntityModel para cada MedicoDTO
                        linkTo(methodOn(MedicoController.class).obtenerMedicoPorId(medicoDTO.getId())).withSelfRel(), // Enlace al propio recurso
                        linkTo(methodOn(MedicoController.class).obtenerMedicos()).withRel("medicos"), // Enlace a la colección de médicos
                        linkTo(methodOn(DisponibilidadController.class).obtenerDisponibilidadesPorMedico(medicoDTO.getId())).withRel("disponibilidades") // Enlace a sus disponibilidades
                        // Puedes añadir enlaces a 'update' y 'delete' si son relevantes aquí y el cliente tiene los permisos
                ))
                .collect(Collectors.toList());

        // Envuelve la lista de EntityModel en un CollectionModel y añade un enlace a la colección general
        return ResponseEntity.ok(CollectionModel.of(medicoModels,
                linkTo(methodOn(MedicoController.class).obtenerMedicos()).withSelfRel() // Enlace a la propia colección
        ));
    }

    // Nuevo endpoint para obtener un médico por ID y devolverlo con enlaces HATEOAS
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EntityModel<MedicoResponseDTO>> obtenerMedicoPorId(@PathVariable Long id) {
        MedicoResponseDTO medicoDTO = medicoService.obtenerPorIdHateoas(id); // Nuevo método en servicio
        if (medicoDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(EntityModel.of(medicoDTO,
                linkTo(methodOn(MedicoController.class).obtenerMedicoPorId(id)).withSelfRel(),
                linkTo(methodOn(MedicoController.class).obtenerMedicos()).withRel("medicos"),
                linkTo(methodOn(DisponibilidadController.class).obtenerDisponibilidadesPorMedico(id)).withRel("disponibilidades"),
                // Enlaces para acciones: solo si el usuario tiene permiso (ej. el propio médico o un admin/coordinador)
                linkTo(methodOn(MedicoController.class).actualizarMedico(id, null)).withRel("update-medico"),
                linkTo(methodOn(MedicoController.class).eliminarMedico(id)).withRel("delete-medico")
        ));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDINADOR')")
    @PostMapping("/crearmedico")
    // Cambiar el tipo de retorno para HATEOAS
    public ResponseEntity<EntityModel<MedicoResponseDTO>> crearMedico(@RequestBody MedicoDTO medicoDTO) {
        // Tu servicio debería retornar ahora un MedicoResponseDTO (con el ID generado)
        MedicoResponseDTO nuevoMedico = medicoService.crearMedicoHateoas(medicoDTO); // Nuevo método en servicio

        return ResponseEntity
                .created(linkTo(methodOn(MedicoController.class).obtenerMedicoPorId(nuevoMedico.getId())).toUri())
                .body(EntityModel.of(nuevoMedico,
                        linkTo(methodOn(MedicoController.class).obtenerMedicoPorId(nuevoMedico.getId())).withSelfRel(),
                        linkTo(methodOn(MedicoController.class).obtenerMedicos()).withRel("medicos")
                ));
    }

    // Nuevo endpoint para actualizar un médico
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDINADOR')")
    public ResponseEntity<EntityModel<MedicoResponseDTO>> actualizarMedico(@PathVariable Long id, @RequestBody MedicoDTO medicoDTO) {
        MedicoResponseDTO medicoActualizado = medicoService.actualizarMedicoHateoas(id, medicoDTO); // Nuevo método en servicio
        if (medicoActualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(EntityModel.of(medicoActualizado,
                linkTo(methodOn(MedicoController.class).obtenerMedicoPorId(id)).withSelfRel(),
                linkTo(methodOn(MedicoController.class).obtenerMedicos()).withRel("medicos")
        ));
    }

    // Nuevo endpoint para eliminar un médico
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDINADOR')")
    public ResponseEntity<Void> eliminarMedico(@PathVariable Long id) {
        medicoService.eliminarMedico(id); // Este método no devuelve un DTO
        return ResponseEntity.noContent().build();
    }

    // El endpoint de obtenerMedicosConFranjas también debería ajustarse para devolver CollectionModel<EntityModel<MedicoFranjasDTO>>
    // Pero para mantenerlo simple por ahora, lo dejaremos como está o lo actualizamos si lo necesitas.
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/confranjas")
    public ResponseEntity<List<MedicoFranjasDTO>> obtenerMedicosConFranjas() {
        List<MedicoFranjasDTO> medicosConFranjas = medicoService.listarMedicosConFranjas();
        return ResponseEntity.ok(medicosConFranjas);
    }
}

