// Ubicación: src/main/java/com/citasalud/backend/controller/DisponibilidadController.java
package com.citasalud.backend.controller;

import com.citasalud.backend.dto.DisponibilidadDTO;
import com.citasalud.backend.service.DisponibilidadService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/franjas")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "BearerAuth")
public class DisponibilidadController {
    private final DisponibilidadService disponibilidadService;

    public DisponibilidadController(DisponibilidadService disponibilidadService) {
        this.disponibilidadService = disponibilidadService;
    }

    @PostMapping(value = "/{medicoId}", produces = MediaTypes.HAL_JSON_VALUE)
    @PreAuthorize("hasAnyRole('MEDICO', 'COORDINADOR')")
    public ResponseEntity<EntityModel<DisponibilidadDTO>> agregarFranja(@Valid @RequestBody DisponibilidadDTO dto, @PathVariable("medicoId") Long medicoId) {
        DisponibilidadDTO nuevaFranja = disponibilidadService.agregarFranjaHateoas(dto, medicoId);
        if (nuevaFranja == null || nuevaFranja.getDisponibilidadId() == null) {
            return ResponseEntity.status(500).build(); // Error interno si el servicio no devuelve un objeto válido
        }
        return ResponseEntity
                .created(linkTo(methodOn(DisponibilidadController.class).obtenerFranjaPorId(nuevaFranja.getDisponibilidadId())).toUri())
                .body(EntityModel.of(nuevaFranja,
                        linkTo(methodOn(DisponibilidadController.class).obtenerFranjaPorId(nuevaFranja.getDisponibilidadId())).withSelfRel()
                ));
    }

    @GetMapping(value = "/medico/{medicoId}", produces = MediaTypes.HAL_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CollectionModel<EntityModel<DisponibilidadDTO>>> obtenerDisponibilidadesPorMedico(@PathVariable Long medicoId) {
        List<DisponibilidadDTO> franjas = disponibilidadService.obtenerDisponibilidadesPorMedico(medicoId);
        List<EntityModel<DisponibilidadDTO>> franjaModels = franjas.stream()
                .map(franjaDTO -> EntityModel.of(franjaDTO,
                        linkTo(methodOn(DisponibilidadController.class).obtenerFranjaPorId(franjaDTO.getDisponibilidadId())).withSelfRel(),
                        linkTo(methodOn(MedicoController.class).obtenerMedicoPorId(medicoId)).withRel("medico")
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(franjaModels,
                linkTo(methodOn(DisponibilidadController.class).obtenerDisponibilidadesPorMedico(medicoId)).withSelfRel()
        ));
    }

    @GetMapping(value = "/listarfranjas", produces = MediaTypes.HAL_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CollectionModel<EntityModel<DisponibilidadDTO>>> obtenerFranjas() {
        List<DisponibilidadDTO> franjas = disponibilidadService.listarFranjas();
        List<EntityModel<DisponibilidadDTO>> franjaModels = franjas.stream()
                .map(franjaDTO -> EntityModel.of(franjaDTO,
                        linkTo(methodOn(DisponibilidadController.class).obtenerFranjaPorId(franjaDTO.getDisponibilidadId())).withSelfRel()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(franjaModels,
                linkTo(methodOn(DisponibilidadController.class).obtenerFranjas()).withSelfRel()
        ));
    }

    @GetMapping(value = "/{idFranja}", produces = MediaTypes.HAL_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EntityModel<DisponibilidadDTO>> obtenerFranjaPorId(@PathVariable("idFranja") Long idFranja) {
        DisponibilidadDTO franjaDTO = disponibilidadService.obtenerFranjaPorIdHateoas(idFranja);
        if (franjaDTO == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(EntityModel.of(franjaDTO,
                linkTo(methodOn(DisponibilidadController.class).obtenerFranjaPorId(idFranja)).withSelfRel()
        ));
    }

    @PutMapping(value = "/{idFranja}", produces = MediaTypes.HAL_JSON_VALUE)
    @PreAuthorize("hasAnyRole('MEDICO', 'COORDINADOR', 'ADMINISTRADOR')")
    public ResponseEntity<EntityModel<DisponibilidadDTO>> actualizarFranja(@PathVariable("idFranja") Long franjaId, @Valid @RequestBody DisponibilidadDTO dto) {
        DisponibilidadDTO franjaActualizada = disponibilidadService.actualizarFranjaHateoas(franjaId, dto);
        if(franjaActualizada == null || franjaActualizada.getDisponibilidadId() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(EntityModel.of(franjaActualizada,
                linkTo(methodOn(DisponibilidadController.class).obtenerFranjaPorId(franjaActualizada.getDisponibilidadId())).withSelfRel()
        ));
    }

    @DeleteMapping("/{idFranja}")
    @PreAuthorize("hasAnyRole('MEDICO', 'COORDINADOR', 'ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarFranja(@PathVariable("idFranja") Long franjaId) {
        disponibilidadService.eliminarFranja(franjaId);
        return ResponseEntity.noContent().build();
    }
}