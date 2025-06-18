// Ubicación: src/test/java/com/citasalud/backend/controller/DisponibilidadControllerTest.java
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DisponibilidadControllerTest {

    @Mock
    private DisponibilidadService disponibilidadService;

    @InjectMocks
    private DisponibilidadController disponibilidadController;

    @BeforeEach
    void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    // --- Tests para agregarFranja ---

    @Test
    void agregarFranja_conDatosValidos_debeRetornarCreated() {
        DisponibilidadDTO dtoEntrada = DataProvider.crearDisponibilidadDTOValida();
        DisponibilidadDTO dtoSalida = DataProvider.crearDisponibilidadDTOValida();
        dtoSalida.setDisponibilidadId(1L);
        when(disponibilidadService.agregarFranjaHateoas(any(DisponibilidadDTO.class), anyLong())).thenReturn(dtoSalida);

        ResponseEntity<?> response = disponibilidadController.agregarFranja(dtoEntrada, 1L);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void agregarFranja_cuandoServicioDevuelveNull_debeRetornarError500() {
        when(disponibilidadService.agregarFranjaHateoas(any(DisponibilidadDTO.class), anyLong())).thenReturn(null);

        ResponseEntity<?> response = disponibilidadController.agregarFranja(new DisponibilidadDTO(), 1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void agregarFranja_cuandoServicioDevuelveDtoSinId_debeRetornarError500() {
        DisponibilidadDTO dtoSinId = DataProvider.crearDisponibilidadDTOValida();
        dtoSinId.setDisponibilidadId(null); // Forzamos que el ID sea nulo
        when(disponibilidadService.agregarFranjaHateoas(any(DisponibilidadDTO.class), anyLong())).thenReturn(dtoSinId);

        ResponseEntity<?> response = disponibilidadController.agregarFranja(new DisponibilidadDTO(), 1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // --- Tests para obtenerDisponibilidadesPorMedico ---

    @Test
    void obtenerDisponibilidadesPorMedico_conResultados_debeRetornarOk() {
        when(disponibilidadService.obtenerDisponibilidadesPorMedico(1L)).thenReturn(DataProvider.crearListaDisponibilidadDTO());

        ResponseEntity<?> response = disponibilidadController.obtenerDisponibilidadesPorMedico(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void obtenerDisponibilidadesPorMedico_cuandoNoHayResultados_debeRetornarOkConListaVacia() {
        when(disponibilidadService.obtenerDisponibilidadesPorMedico(1L)).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = disponibilidadController.obtenerDisponibilidadesPorMedico(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    // --- Tests para obtenerFranjas ---

    @Test
    void obtenerFranjas_conResultados_debeRetornarOk() {
        when(disponibilidadService.listarFranjas()).thenReturn(DataProvider.crearListaDisponibilidadDTO());

        ResponseEntity<?> response = disponibilidadController.obtenerFranjas();

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // --- Tests para obtenerFranjaPorId ---

    @Test
    void obtenerFranjaPorId_cuandoExiste_debeRetornarOk() {
        when(disponibilidadService.obtenerFranjaPorIdHateoas(1L)).thenReturn(new DisponibilidadDTO());

        ResponseEntity<?> response = disponibilidadController.obtenerFranjaPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void obtenerFranjaPorId_cuandoNoExiste_debeRetornarNotFound() {
        when(disponibilidadService.obtenerFranjaPorIdHateoas(99L)).thenReturn(null);

        ResponseEntity<?> response = disponibilidadController.obtenerFranjaPorId(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // --- Tests para actualizarFranja ---

    @Test
    void actualizarFranja_cuandoExiste_debeRetornarOk() {
        DisponibilidadDTO dtoEntrada = DataProvider.crearDisponibilidadDTOValida();
        DisponibilidadDTO dtoSalida = DataProvider.crearDisponibilidadDTOValida();
        dtoSalida.setDisponibilidadId(1L);
        when(disponibilidadService.actualizarFranjaHateoas(anyLong(), any(DisponibilidadDTO.class))).thenReturn(dtoSalida);

        ResponseEntity<?> response = disponibilidadController.actualizarFranja(1L, dtoEntrada);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void actualizarFranja_cuandoNoExiste_debeRetornarNotFound() {
        when(disponibilidadService.actualizarFranjaHateoas(anyLong(), any(DisponibilidadDTO.class))).thenReturn(null);

        ResponseEntity<?> response = disponibilidadController.actualizarFranja(99L, new DisponibilidadDTO());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void actualizarFranja_cuandoServicioDevuelveDtoSinId_debeRetornarNotFound() {
        DisponibilidadDTO dtoSinId = DataProvider.crearDisponibilidadDTOValida();
        dtoSinId.setDisponibilidadId(null); // Forzamos que el ID sea nulo
        when(disponibilidadService.actualizarFranjaHateoas(anyLong(), any(DisponibilidadDTO.class))).thenReturn(dtoSinId);

        ResponseEntity<?> response = disponibilidadController.actualizarFranja(99L, new DisponibilidadDTO());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // --- Test para eliminarFranja ---

    @Test
    void eliminarFranja_debeRetornarNoContent() {
        doNothing().when(disponibilidadService).eliminarFranja(1L);

        ResponseEntity<Void> response = disponibilidadController.eliminarFranja(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}