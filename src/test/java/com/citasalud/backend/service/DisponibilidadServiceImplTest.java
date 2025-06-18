// Ubicación: src/test/java/com/citasalud/backend/service/DisponibilidadServiceImplTest.java
package com.citasalud.backend.service;

import com.citasalud.backend.DataProvider;
import com.citasalud.backend.domain.Disponibilidad;
import com.citasalud.backend.domain.Medico;
import com.citasalud.backend.dto.DisponibilidadDTO;
import com.citasalud.backend.repository.DisponibilidadRepository;
import com.citasalud.backend.repository.MedicoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisponibilidadServiceImplTest {

    @Mock
    private DisponibilidadRepository disponibilidadRepository;

    @Mock
    private MedicoRepository medicoRepository;

    @InjectMocks
    private DisponibilidadServiceImpl disponibilidadService;

    // --- Tests existentes (verificados) ---

    @Test
    void agregarFranja_cuandoMedicoExiste_debeAsociarMedicoYGuardarFranja() {
        long medicoId = 1L;
        DisponibilidadDTO dto = DataProvider.crearDisponibilidadDTOValida();
        Medico medicoEncontrado = new Medico();
        medicoEncontrado.setId(medicoId);
        when(medicoRepository.findById(medicoId)).thenReturn(Optional.of(medicoEncontrado));
        disponibilidadService.agregarFranja(dto, medicoId);
        ArgumentCaptor<Disponibilidad> captor = ArgumentCaptor.forClass(Disponibilidad.class);
        verify(disponibilidadRepository).save(captor.capture());
        assertEquals(medicoId, captor.getValue().getMedico().getId());
    }

    @Test
    void agregarFranja_cuandoMedicoNoExiste_debeLanzarRuntimeException() {
        long medicoIdInexistente = 99L;
        DisponibilidadDTO dto = DataProvider.crearDisponibilidadDTOValida();
        when(medicoRepository.findById(medicoIdInexistente)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> disponibilidadService.agregarFranja(dto, medicoIdInexistente));
    }

    @Test
    void listarFranjas_debeRetornarListaDeDisponibilidadDTO() {
        when(disponibilidadRepository.findAll()).thenReturn(DataProvider.crearListaDisponibilidadEntidad());
        List<DisponibilidadDTO> resultado = disponibilidadService.listarFranjas();
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
    }

    @Test
    void eliminarFranja_cuandoFranjaExiste_debeLlamarADeleteById() {
        long franjaIdExistente = 1L;
        when(disponibilidadRepository.existsById(franjaIdExistente)).thenReturn(true);
        disponibilidadService.eliminarFranja(franjaIdExistente);
        verify(disponibilidadRepository).deleteById(franjaIdExistente);
    }

    @Test
    void eliminarFranja_cuandoFranjaNoExiste_debeLanzarRuntimeException() {
        long franjaIdInexistente = 99L;
        when(disponibilidadRepository.existsById(franjaIdInexistente)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> disponibilidadService.eliminarFranja(franjaIdInexistente));
    }

    @Test
    void actualizarFranja_cuandoFranjaExiste_debeActualizarYRetornarDTO() {
        long franjaIdExistente = 1L;
        DisponibilidadDTO datosNuevosDTO = DataProvider.crearDisponibilidadDTOValida();
        when(disponibilidadRepository.findById(franjaIdExistente)).thenReturn(Optional.of(new Disponibilidad()));
        when(disponibilidadRepository.save(any(Disponibilidad.class))).thenAnswer(i -> i.getArgument(0));
        DisponibilidadDTO resultadoDTO = disponibilidadService.actualizarFranja(franjaIdExistente, datosNuevosDTO);
        assertNotNull(resultadoDTO);
    }

    @Test
    void actualizarFranja_cuandoFranjaNoExiste_debeLanzarRuntimeException() {
        long franjaIdInexistente = 99L;
        DisponibilidadDTO datosNuevosDTO = DataProvider.crearDisponibilidadDTOValida();
        when(disponibilidadRepository.findById(franjaIdInexistente)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> disponibilidadService.actualizarFranja(franjaIdInexistente, datosNuevosDTO));
    }

    // --- NUEVOS TESTS PARA MÉTODOS HATEOAS Y COBERTURA RESTANTE ---

    @Test
    void agregarFranjaHateoas_cuandoMedicoNoExiste_debeLanzarExcepcion() {
        when(medicoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> {
            disponibilidadService.agregarFranjaHateoas(new DisponibilidadDTO(), 99L);
        });
    }

    @Test
    void agregarFranjaHateoas_cuandoMedicoExiste_debeGuardarYRetornarDto() {
        DisponibilidadDTO dto = DataProvider.crearDisponibilidadDTOValida();
        Medico medico = new Medico();
        Disponibilidad franjaGuardada = new Disponibilidad();
        franjaGuardada.setDisponibilidadId(1L);

        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));
        when(disponibilidadRepository.save(any(Disponibilidad.class))).thenReturn(franjaGuardada);

        DisponibilidadDTO resultado = disponibilidadService.agregarFranjaHateoas(dto, 1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getDisponibilidadId());
    }

    @Test
    void obtenerDisponibilidadesPorMedico_cuandoMedicoNoExiste_debeRetornarListaVacia() {
        when(medicoRepository.findById(99L)).thenReturn(Optional.empty());
        List<DisponibilidadDTO> resultado = disponibilidadService.obtenerDisponibilidadesPorMedico(99L);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerDisponibilidadesPorMedico_cuandoMedicoExiste_debeRetornarListaDto() {
        Medico medico = DataProvider.crearListaMedicosConRelaciones().get(0);
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));

        List<DisponibilidadDTO> resultado = disponibilidadService.obtenerDisponibilidadesPorMedico(1L);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
    }

    @Test
    void obtenerFranjaPorIdHateoas_cuandoNoExiste_debeRetornarNull() {
        when(disponibilidadRepository.findById(99L)).thenReturn(Optional.empty());
        DisponibilidadDTO resultado = disponibilidadService.obtenerFranjaPorIdHateoas(99L);
        assertNull(resultado);
    }

    @Test
    void obtenerFranjaPorIdHateoas_cuandoExiste_debeRetornarDto() {
        Disponibilidad franja = DataProvider.crearListaDisponibilidadEntidad().get(0);
        when(disponibilidadRepository.findById(1L)).thenReturn(Optional.of(franja));
        DisponibilidadDTO resultado = disponibilidadService.obtenerFranjaPorIdHateoas(1L);
        assertNotNull(resultado);
    }

    @Test
    void actualizarFranjaHateoas_cuandoNoExiste_debeLanzarExcepcion() {
        when(disponibilidadRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> {
            disponibilidadService.actualizarFranjaHateoas(99L, new DisponibilidadDTO());
        });
    }

    @Test
    void actualizarFranjaHateoas_cuandoExiste_debeActualizarYRetornarDto() {
        DisponibilidadDTO dto = DataProvider.crearDisponibilidadDTOValida();
        Disponibilidad franjaExistente = new Disponibilidad();

        when(disponibilidadRepository.findById(1L)).thenReturn(Optional.of(franjaExistente));
        when(disponibilidadRepository.save(any(Disponibilidad.class))).thenReturn(franjaExistente);

        DisponibilidadDTO resultado = disponibilidadService.actualizarFranjaHateoas(1L, dto);
        assertNotNull(resultado);
    }
}