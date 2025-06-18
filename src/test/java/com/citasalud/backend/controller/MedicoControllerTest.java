// Ubicación: src/test/java/com/citasalud/backend/controller/MedicoControllerTest.java
package com.citasalud.backend.controller;

import com.citasalud.backend.DataProvider;
import com.citasalud.backend.dto.MedicoDTO;
import com.citasalud.backend.dto.MedicoFranjasDTO;
import com.citasalud.backend.dto.MedicoResponseDTO;
import com.citasalud.backend.service.MedicoService;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

// CORRECCIÓN CLAVE: Usamos Mockito puro, sin contexto de Spring
@ExtendWith(MockitoExtension.class)
class MedicoControllerTest {

    // Se usa @Mock de Mockito, que no necesita un contexto de Spring
    @Mock
    private MedicoService medicoService;

    // @InjectMocks creará una instancia real de MedicoController
    // e inyectará el mock de medicoService en su constructor.
    @InjectMocks
    private MedicoController medicoController;

    @BeforeEach
    void setUp() {
        // Para que los enlaces HATEOAS funcionen, necesitan un contexto de request.
        // Lo simulamos manualmente.
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void obtenerMedicos_deberiaRetornarOkConDatos() {
        // ARRANGE
        when(medicoService.obtenerTodosHateoas()).thenReturn(DataProvider.crearListaMedicosResponseDTO());

        // ACT
        ResponseEntity<?> response = medicoController.obtenerMedicos();

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void crearMedico_conMedicoValido_deberiaRetornarCreated() {
        // ARRANGE
        MedicoDTO medicoParaCrear = DataProvider.crearMedicoDTO();
        MedicoResponseDTO medicoCreado = new MedicoResponseDTO(1L, "Carlos", "Santana", "carlos.santana@example.com", "CC", "12345678", 1L, "Cardiología", 2L, "MÉDICO");
        when(medicoService.crearMedicoHateoas(any(MedicoDTO.class))).thenReturn(medicoCreado);

        // ACT
        ResponseEntity<?> response = medicoController.crearMedico(medicoParaCrear);

        // ASSERT
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getHeaders().getLocation());
    }

    @Test
    void obtenerMedicoPorId_cuandoExiste_debeRetornarOk() {
        // ARRANGE
        long medicoId = 1L;
        when(medicoService.obtenerPorIdHateoas(medicoId)).thenReturn(DataProvider.crearListaMedicosResponseDTO().get(0));

        // ACT
        ResponseEntity<?> response = medicoController.obtenerMedicoPorId(medicoId);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void obtenerMedicoPorId_cuandoNoExiste_debeRetornarNotFound() {
        // ARRANGE
        long medicoId = 99L;
        when(medicoService.obtenerPorIdHateoas(medicoId)).thenReturn(null);

        // ACT
        ResponseEntity<?> response = medicoController.obtenerMedicoPorId(medicoId);

        // ASSERT
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void actualizarMedico_cuandoExiste_debeRetornarOk() {
        // ARRANGE
        long medicoId = 1L;
        MedicoDTO medicoUpdateDTO = DataProvider.crearMedicoDTO();
        when(medicoService.actualizarMedicoHateoas(eq(medicoId), any(MedicoDTO.class)))
                .thenReturn(DataProvider.crearListaMedicosResponseDTO().get(0));

        // ACT
        ResponseEntity<?> response = medicoController.actualizarMedico(medicoId, medicoUpdateDTO);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void actualizarMedico_cuandoNoExiste_debeRetornarNotFound() {
        // ARRANGE
        long medicoId = 99L;
        MedicoDTO medicoUpdateDTO = DataProvider.crearMedicoDTO();
        when(medicoService.actualizarMedicoHateoas(eq(medicoId), any(MedicoDTO.class)))
                .thenReturn(null);

        // ACT
        ResponseEntity<?> response = medicoController.actualizarMedico(medicoId, medicoUpdateDTO);

        // ASSERT
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void eliminarMedico_cuandoExiste_debeRetornarNoContent() {
        // ARRANGE
        long medicoId = 1L;
        doNothing().when(medicoService).eliminarMedico(medicoId);

        // ACT
        ResponseEntity<Void> response = medicoController.eliminarMedico(medicoId);

        // ASSERT
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void obtenerMedicosConFranjas_debeRetornarOkConDatos() {
        // ARRANGE
        when(medicoService.listarMedicosConFranjas()).thenReturn(DataProvider.crearListaMedicosConFranjasDTO());

        // ACT
        ResponseEntity<List<MedicoFranjasDTO>> response = medicoController.obtenerMedicosConFranjas();

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }
}