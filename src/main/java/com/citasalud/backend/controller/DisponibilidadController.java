package com.citasalud.backend.controller;

import com.citasalud.backend.dto.DisponibilidadDTO;
import com.citasalud.backend.service.DisponibilidadService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

// HATEOAS Imports
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/franjas")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "BearerAuth")
public class DisponibilidadController {
    private final DisponibilidadService franjaHorariaService;

    public DisponibilidadController(DisponibilidadService franjaHorariaService) {
        this.franjaHorariaService = franjaHorariaService;
    }

    // HU002 - Agregar franja horaria a un médico
    @PostMapping("/{medicoId}") // Cambiado a {medicoId} para ser más explícito
    @PreAuthorize("hasAnyRole('MEDICO', 'COORDINADOR')")
    public ResponseEntity<EntityModel<DisponibilidadDTO>> agregarFranja(@Valid @RequestBody DisponibilidadDTO dto,
                                                                        @PathVariable("medicoId") Long medicoId) {
        DisponibilidadDTO nuevaFranja = franjaHorariaService.agregarFranjaHateoas(dto, medicoId); // Nuevo método en servicio

        return ResponseEntity
                .created(linkTo(methodOn(DisponibilidadController.class).obtenerFranjaPorId(nuevaFranja.getDisponibilidadId())).toUri())
                .body(EntityModel.of(nuevaFranja,
                        linkTo(methodOn(DisponibilidadController.class).obtenerFranjaPorId(nuevaFranja.getDisponibilidadId())).withSelfRel(),
                        linkTo(methodOn(DisponibilidadController.class).obtenerFranjas()).withRel("franjas")
                ));
    }

    @PreAuthorize("isAuthenticated()") // O los roles apropiados
    @GetMapping("/medico/{medicoId}") // Ruta para obtener franjas de un médico específico
    public ResponseEntity<CollectionModel<EntityModel<DisponibilidadDTO>>> obtenerDisponibilidadesPorMedico(@PathVariable Long medicoId) {
        List<DisponibilidadDTO> franjas = franjaHorariaService.obtenerDisponibilidadesPorMedico(medicoId);

        List<EntityModel<DisponibilidadDTO>> franjaModels = franjas.stream()
                .map(franjaDTO -> EntityModel.of(franjaDTO,
                        linkTo(methodOn(DisponibilidadController.class).obtenerFranjaPorId(franjaDTO.getDisponibilidadId())).withSelfRel(),
                        // Enlace al médico asociado (asegúrate de que tu DTO de Disponibilidad tenga el ID del médico si lo quieres aquí)
                        linkTo(methodOn(MedicoController.class).obtenerMedicoPorId(medicoId)).withRel("medico")
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(franjaModels,
                linkTo(methodOn(DisponibilidadController.class).obtenerDisponibilidadesPorMedico(medicoId)).withSelfRel() // Enlace a esta colección específica
        ));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/listarfranjas")
    public ResponseEntity<CollectionModel<EntityModel<DisponibilidadDTO>>> obtenerFranjas() {
        List<DisponibilidadDTO> franjas = franjaHorariaService.listarFranjas();

        List<EntityModel<DisponibilidadDTO>> franjaModels = franjas.stream()
                .map(franjaDTO -> EntityModel.of(franjaDTO,
                        linkTo(methodOn(DisponibilidadController.class).obtenerFranjaPorId(franjaDTO.getDisponibilidadId())).withSelfRel(),
                        linkTo(methodOn(MedicoController.class).obtenerMedicoPorId(franjaDTO.getDisponibilidadId() /* Aquí necesitas el medicoId asociado a la franja, tu DTO no lo tiene */)).withRel("medico-asociado") // Esto es conceptual, tu DTO de Disponibilidad no tiene medicoId
                        // Puedes añadir enlaces a 'update' y 'delete' aquí
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(franjaModels,
                linkTo(methodOn(DisponibilidadController.class).obtenerFranjas()).withSelfRel()
        ));
    }

    // Nuevo endpoint para obtener una franja por ID
    @GetMapping("/{idFranja}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EntityModel<DisponibilidadDTO>> obtenerFranjaPorId(@PathVariable("idFranja") Long idFranja) {
        DisponibilidadDTO franjaDTO = franjaHorariaService.obtenerFranjaPorIdHateoas(idFranja); // Nuevo método
        if (franjaDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(EntityModel.of(franjaDTO,
                linkTo(methodOn(DisponibilidadController.class).obtenerFranjaPorId(idFranja)).withSelfRel(),
                linkTo(methodOn(DisponibilidadController.class).obtenerFranjas()).withRel("franjas"),
                linkTo(methodOn(DisponibilidadController.class).actualizarFranja(idFranja, null)).withRel("update-franja"),
                linkTo(methodOn(DisponibilidadController.class).eliminarFranja(idFranja)).withRel("delete-franja")
        ));
    }

    @PreAuthorize("hasAnyRole('MEDICO', 'COORDINADOR', 'ADMINISTRADOR')")
    @DeleteMapping("/{idFranja}")
    public ResponseEntity<Void> eliminarFranja(@PathVariable("idFranja") Long franjaId) {
        franjaHorariaService.eliminarFranja(franjaId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('MEDICO', 'COORDINADOR', 'ADMINISTRADOR')")
    @PutMapping("/{idFranja}")
    public ResponseEntity<EntityModel<DisponibilidadDTO>> actualizarFranja(@PathVariable("idFranja") Long franjaId,
            @Valid @RequestBody DisponibilidadDTO dto) {
        DisponibilidadDTO franjaActualizada = franjaHorariaService.actualizarFranjaHateoas(franjaId, dto); // Nuevo método en servicio
        return ResponseEntity.ok(EntityModel.of(franjaActualizada,
                linkTo(methodOn(DisponibilidadController.class).obtenerFranjaPorId(franjaActualizada.getDisponibilidadId())).withSelfRel(),
                linkTo(methodOn(DisponibilidadController.class).obtenerFranjas()).withRel("franjas")
        ));
    }
}
