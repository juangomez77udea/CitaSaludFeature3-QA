package com.citasalud.backend.controller;

import com.citasalud.backend.DataProvider;
import com.citasalud.backend.dto.DisponibilidadDTO;
import com.citasalud.backend.service.DisponibilidadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisponibilidadControllerTest {

    @Mock
    private DisponibilidadService disponibilidadService;

    @InjectMocks
    private DisponibilidadController disponibilidadController;

    private DisponibilidadDTO dtoValido;

    @BeforeEach
    void setUp() {
        // Necesario para que Spring HATEOAS (linkTo) funcione en un entorno de prueba unitaria
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        // Crear un DTO base para usar en los tests
        dtoValido = DataProvider.crearDisponibilidadDTOValida();
        dtoValido.setDisponibilidadId(1L);
    }

    // --- Tests para agregarFranja ---

    @Test
    void agregarFranja_conDatosValidos_debeRetornarCreatedConHateoas() {
        // GIVEN: El servicio devuelve un DTO con ID, que es lo que el controlador espera.
        when(disponibilidadService.agregarFranjaHateoas(any(DisponibilidadDTO.class), anyLong())).thenReturn(dtoValido);

        // WHEN
        ResponseEntity<EntityModel<DisponibilidadDTO>> response = disponibilidadController.agregarFranja(dtoValido, 1L);

        // THEN
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getHeaders().getLocation()); // Verificar que la cabecera Location se creó
        assertTrue(response.getBody().getLinks().hasLink("self"));
    }

    @Test
    void agregarFranja_cuandoServicioDevuelveNull_debeLanzarNullPointerException() {
        // GIVEN: El servicio devuelve null, forzando el fallo del controlador
        when(disponibilidadService.agregarFranjaHateoas(any(DisponibilidadDTO.class), anyLong())).thenReturn(null);

        // WHEN & THEN: Verificamos que el controlador, al no tener chequeo de nulos, lanza la excepción
        assertThrows(NullPointerException.class, () -> {
            disponibilidadController.agregarFranja(new DisponibilidadDTO(), 1L);
        });
    }

    // --- Tests para actualizarFranja ---

    @Test
    void actualizarFranja_cuandoExiste_debeRetornarOkConHateoas() {
        // GIVEN
        when(disponibilidadService.actualizarFranjaHateoas(anyLong(), any(DisponibilidadDTO.class))).thenReturn(dtoValido);

        // WHEN
        ResponseEntity<EntityModel<DisponibilidadDTO>> response = disponibilidadController.actualizarFranja(1L, dtoValido);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getLinks().hasLink("self"));
    }

    @Test
    void actualizarFranja_cuandoNoExiste_debeLanzarNullPointerException() {
        // GIVEN: El servicio devuelve null, simulando que no se encontró el recurso
        when(disponibilidadService.actualizarFranjaHateoas(anyLong(), any(DisponibilidadDTO.class))).thenReturn(null);

        // WHEN & THEN: El controlador actual intenta usar el objeto nulo, por lo que debe lanzar una excepción.
        assertThrows(NullPointerException.class, () -> {
            disponibilidadController.actualizarFranja(99L, new DisponibilidadDTO());
        });
    }

    // --- Tests para obtenerFranjaPorId ---

    @Test
    void obtenerFranjaPorId_cuandoNoExiste_debeRetornarNotFound() {
        // GIVEN
        when(disponibilidadService.obtenerFranjaPorIdHateoas(99L)).thenReturn(null);

        // WHEN
        ResponseEntity<EntityModel<DisponibilidadDTO>> response = disponibilidadController.obtenerFranjaPorId(99L);

        // THEN: Tu controlador maneja este caso correctamente
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // --- Tests para obtenerDisponibilidadesPorMedico ---

    @Test
    void obtenerDisponibilidadesPorMedico_cuandoNoHayResultados_debeRetornarOkConCollectionModelVacio() {
        // GIVEN
        when(disponibilidadService.obtenerDisponibilidadesPorMedico(1L)).thenReturn(Collections.emptyList());

        // WHEN
        ResponseEntity<CollectionModel<EntityModel<DisponibilidadDTO>>> response = disponibilidadController.obtenerDisponibilidadesPorMedico(1L);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getContent().isEmpty(), "El contenido del CollectionModel debería estar vacío");
    }

    // --- Tests para eliminarFranja ---

    @Test
    void eliminarFranja_debeRetornarNoContent() {
        // GIVEN
        doNothing().when(disponibilidadService).eliminarFranja(1L);

        // WHEN
        ResponseEntity<Void> response = disponibilidadController.eliminarFranja(1L);

        // THEN
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}