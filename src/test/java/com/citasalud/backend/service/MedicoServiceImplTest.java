package com.citasalud.backend.service;

import com.citasalud.backend.DataProvider;
import com.citasalud.backend.domain.Especialidad;
import com.citasalud.backend.domain.Medico;
import com.citasalud.backend.domain.Rol;
import com.citasalud.backend.dto.MedicoDTO;
import com.citasalud.backend.dto.MedicoFranjasDTO;
import com.citasalud.backend.dto.MedicoResponseDTO;
import com.citasalud.backend.repository.EspecialidadRepository;
import com.citasalud.backend.repository.MedicoRepository;
import com.citasalud.backend.repository.RolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicoServiceImplTest {

    @Mock
    private MedicoRepository medicoRepository;
    @Mock
    private EspecialidadRepository especialidadRepository;
    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private MedicoServiceImpl medicoService;

    private MedicoDTO medicoDTO;
    private Medico medico;

    @BeforeEach
    void setUp() {
        medicoDTO = DataProvider.crearMedicoDTO();
        medico = DataProvider.crearListaMedicosConRelaciones().get(0);
    }

    // --- Tus Tests Existentes ---
    @Test
    void obtenerTodosHateoas_debeRetornarLista() {
        when(medicoRepository.findAll()).thenReturn(DataProvider.crearListaMedicosEntidad());
        List<MedicoResponseDTO> resultado = medicoService.obtenerTodosHateoas();
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
    }

    @Test
    void obtenerPorIdHateoas_cuandoExiste_debeRetornarDto() {
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));
        MedicoResponseDTO resultado = medicoService.obtenerPorIdHateoas(1L);
        assertNotNull(resultado);
    }

    @Test
    void obtenerPorIdHateoas_cuandoNoExiste_debeRetornarNull() {
        when(medicoRepository.findById(99L)).thenReturn(Optional.empty());
        assertNull(medicoService.obtenerPorIdHateoas(99L));
    }

    @Test
    void crearMedico_conDatosValidos_debeGuardar() {
        when(especialidadRepository.findById(anyLong())).thenReturn(Optional.of(new Especialidad()));
        when(rolRepository.findById(anyLong())).thenReturn(Optional.of(new Rol()));
        medicoService.crearMedico(medicoDTO);
        verify(medicoRepository).save(any(Medico.class));
    }

    @Test
    void crearMedicoHateoas_conDatosValidos_debeGuardarYRetornar() {
        Medico medicoGuardado = new Medico();
        medicoGuardado.setId(1L);
        medicoGuardado.setEspecialidad(new Especialidad());
        medicoGuardado.setRolId(new Rol());
        when(especialidadRepository.findById(anyLong())).thenReturn(Optional.of(new Especialidad()));
        when(rolRepository.findById(anyLong())).thenReturn(Optional.of(new Rol()));
        when(medicoRepository.save(any(Medico.class))).thenReturn(medicoGuardado);
        MedicoResponseDTO resultado = medicoService.crearMedicoHateoas(medicoDTO);
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void crearMedico_conEspecialidadInvalida_debeLanzarExcepcion() {
        when(especialidadRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> medicoService.crearMedico(medicoDTO));
    }

    @Test
    void crearMedico_conRolInvalido_debeLanzarExcepcion() {
        when(especialidadRepository.findById(anyLong())).thenReturn(Optional.of(new Especialidad()));
        when(rolRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> medicoService.crearMedico(medicoDTO));
    }

    @Test
    void actualizarMedicoHateoas_cuandoMedicoNoExiste_debeLanzarExcepcion() {
        when(medicoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> medicoService.actualizarMedicoHateoas(99L, new MedicoDTO()));
    }

    @Test
    void actualizarMedicoHateoas_conNuevosIdsValidos_debeActualizar() {
        Medico medicoExistente = DataProvider.crearListaMedicosConRelaciones().get(0);
        medicoExistente.getEspecialidad().setEspecialidadId(1L);
        medicoExistente.getRolId().setRolId(1L);
        MedicoDTO medicoUpdateDTO = DataProvider.crearMedicoDTO();
        medicoUpdateDTO.setEspecialidadId(2L);
        medicoUpdateDTO.setRolId(2L);
        when(medicoRepository.findById(anyLong())).thenReturn(Optional.of(medicoExistente));
        when(especialidadRepository.findById(2L)).thenReturn(Optional.of(new Especialidad()));
        when(rolRepository.findById(2L)).thenReturn(Optional.of(new Rol()));
        when(medicoRepository.save(any(Medico.class))).thenAnswer(i -> i.getArgument(0));
        medicoService.actualizarMedicoHateoas(1L, medicoUpdateDTO);
        verify(especialidadRepository).findById(2L);
        verify(rolRepository).findById(2L);
    }

    @Test
    void eliminarMedico_cuandoNoExiste_debeLanzarExcepcion() {
        when(medicoRepository.existsById(99L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> medicoService.eliminarMedico(99L));
    }

    @Test
    void eliminarMedico_cuandoExiste_debeLlamarDelete() {
        when(medicoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(medicoRepository).deleteById(1L);
        medicoService.eliminarMedico(1L);
        verify(medicoRepository).deleteById(1L);
    }

    @Test
    void listarMedicosConFranjas_conDatosCompletos_debeMapearTodo() {
        List<Medico> medicosConDatos = DataProvider.crearListaMedicosConRelaciones();
        when(medicoRepository.findAllWithDisponibilidades()).thenReturn(medicosConDatos);
        List<MedicoFranjasDTO> resultado = medicoService.listarMedicosConFranjas();
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        MedicoFranjasDTO medicoDTO = resultado.get(0);
        assertNotNull(medicoDTO.getEspecialidadNombre());
        assertNotNull(medicoDTO.getRolNombre());
        assertFalse(medicoDTO.getFranjasDisponibles().isEmpty());
    }

    @Test
    void listarMedicosConFranjas_conDisponibilidadesNulas_debeRetornarListaVaciaDeFranjas() {
        List<Medico> medicosSinFranjas = DataProvider.crearMedicoConDisponibilidadesNulas();
        when(medicoRepository.findAllWithDisponibilidades()).thenReturn(medicosSinFranjas);
        List<MedicoFranjasDTO> resultado = medicoService.listarMedicosConFranjas();
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        MedicoFranjasDTO dtoResultado = resultado.get(0);
        assertNotNull(dtoResultado.getFranjasDisponibles());
        assertTrue(dtoResultado.getFranjasDisponibles().isEmpty());
    }

    // --- NUEVOS TESTS PARA AUMENTAR COBERTURA ---

    @Test
    void actualizarMedicoHateoas_conIdEspecialidadNulo_noDebeActualizarEspecialidad() {
        medicoDTO.setEspecialidadId(null);
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));
        when(medicoRepository.save(any(Medico.class))).thenReturn(medico);

        medicoService.actualizarMedicoHateoas(1L, medicoDTO);

        verify(especialidadRepository, never()).findById(anyLong());
        verify(medicoRepository).save(any(Medico.class));
    }

    @Test
    void actualizarMedicoHateoas_conMismoIdEspecialidad_noDebeActualizarEspecialidad() {
        medicoDTO.setEspecialidadId(medico.getEspecialidad().getEspecialidadId());
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));
        when(medicoRepository.save(any(Medico.class))).thenReturn(medico);

        medicoService.actualizarMedicoHateoas(1L, medicoDTO);

        verify(especialidadRepository, never()).findById(anyLong());
        verify(medicoRepository).save(any(Medico.class));
    }

    @Test
    void actualizarMedicoHateoas_conIdRolNulo_noDebeActualizarRol() {
        medicoDTO.setRolId(null);
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));
        when(medicoRepository.save(any(Medico.class))).thenReturn(medico);

        medicoService.actualizarMedicoHateoas(1L, medicoDTO);

        verify(rolRepository, never()).findById(anyLong());
        verify(medicoRepository).save(any(Medico.class));
    }

    @Test
    void actualizarMedicoHateoas_conMismoIdRol_noDebeActualizarRol() {
        medicoDTO.setRolId(medico.getRolId().getRolId());
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));
        when(medicoRepository.save(any(Medico.class))).thenReturn(medico);

        medicoService.actualizarMedicoHateoas(1L, medicoDTO);

        verify(rolRepository, never()).findById(anyLong());
        verify(medicoRepository).save(any(Medico.class));
    }
}