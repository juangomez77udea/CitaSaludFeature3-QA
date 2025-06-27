package com.citasalud.backend.service;

import com.citasalud.backend.DataProvider;
import com.citasalud.backend.domain.Especialidad;
import com.citasalud.backend.domain.Medico;
import com.citasalud.backend.domain.Rol;
import com.citasalud.backend.dto.MedicoDTO;
import com.citasalud.backend.dto.MedicoFranjasDTO;
import com.citasalud.backend.dto.MedicoResponseDTO;
import com.citasalud.backend.mapper.MedicoMapper;
import com.citasalud.backend.repository.EspecialidadRepository;
import com.citasalud.backend.repository.MedicoRepository;
import com.citasalud.backend.repository.RolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MedicoServiceImpl medicoService;

    private MedicoDTO medicoDTO;
    private Medico medico;
    private Especialidad especialidad;
    private Rol rol;

    @BeforeEach
    void setUp() {
        medicoDTO = DataProvider.crearMedicoDTO();
        medico = DataProvider.crearListaMedicosConRelaciones().get(0);
        especialidad = medico.getEspecialidad();
        rol = medico.getRolId();
    }

    // --- Pruebas para obtenerTodosHateoas ---
    @Test
    void obtenerTodosHateoas_debeRetornarListaDeMedicos() {
        try (MockedStatic<MedicoMapper> mockedMapper = mockStatic(MedicoMapper.class)) {
            when(medicoRepository.findAll()).thenReturn(List.of(medico));
            mockedMapper.when(() -> MedicoMapper.toResponseDTO(any(Medico.class))).thenReturn(new MedicoResponseDTO());

            List<MedicoResponseDTO> resultado = medicoService.obtenerTodosHateoas();

            assertNotNull(resultado);
            assertFalse(resultado.isEmpty());
        }
    }

    // --- Pruebas para obtenerPorIdHateoas ---
    @Test
    void obtenerPorIdHateoas_cuandoMedicoExiste_debeRetornarDto() {
        try (MockedStatic<MedicoMapper> mockedMapper = mockStatic(MedicoMapper.class)) {
            when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));
            mockedMapper.when(() -> MedicoMapper.toResponseDTO(medico)).thenReturn(new MedicoResponseDTO());

            MedicoResponseDTO resultado = medicoService.obtenerPorIdHateoas(1L);

            assertNotNull(resultado);
        }
    }

    // --- Pruebas para crearMedico ---
    @Test
    void crearMedico_conDatosValidos_debeGuardar() {
        try (MockedStatic<MedicoMapper> mockedMapper = mockStatic(MedicoMapper.class)) {
            mockedMapper.when(() -> MedicoMapper.toEntity(medicoDTO)).thenReturn(medico);
            when(especialidadRepository.findById(anyLong())).thenReturn(Optional.of(especialidad));
            when(rolRepository.findById(anyLong())).thenReturn(Optional.of(rol));

            medicoService.crearMedico(medicoDTO);

            verify(medicoRepository).save(medico);
        }
    }

    // --- Pruebas para crearMedicoHateoas ---
    @Test
    void crearMedicoHateoas_conDatosValidos_debeGuardarYRetornar() {
        try (MockedStatic<MedicoMapper> mockedMapper = mockStatic(MedicoMapper.class)) {
            mockedMapper.when(() -> MedicoMapper.toEntity(any(MedicoDTO.class))).thenReturn(medico);
            mockedMapper.when(() -> MedicoMapper.toResponseDTO(any(Medico.class))).thenReturn(new MedicoResponseDTO());
            when(especialidadRepository.findById(anyLong())).thenReturn(Optional.of(especialidad));
            when(rolRepository.findById(anyLong())).thenReturn(Optional.of(rol));
            when(medicoRepository.save(any(Medico.class))).thenReturn(medico);

            assertNotNull(medicoService.crearMedicoHateoas(medicoDTO));
        }
    }

    // --- Pruebas para actualizarMedicoHateoas ---
    @Test
    void actualizarMedicoHateoas_conDatosValidos_debeActualizarYRetornar() {
        Medico medicoExistente = DataProvider.crearListaMedicosConRelaciones().get(0);
        medicoExistente.getEspecialidad().setEspecialidadId(1L);
        medicoExistente.getRolId().setRolId(1L);

        medicoDTO.setEspecialidadId(2L);
        medicoDTO.setRolId(2L);

        Especialidad nuevaEspecialidad = new Especialidad();
        nuevaEspecialidad.setEspecialidadId(2L);
        Rol nuevoRol = new Rol();
        nuevoRol.setRolId(2L);

        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medicoExistente));
        when(especialidadRepository.findById(2L)).thenReturn(Optional.of(nuevaEspecialidad));
        when(rolRepository.findById(2L)).thenReturn(Optional.of(nuevoRol));
        when(medicoRepository.save(any(Medico.class))).thenReturn(medicoExistente);

        try (MockedStatic<MedicoMapper> mockedMapper = mockStatic(MedicoMapper.class)) {
            mockedMapper.when(() -> MedicoMapper.toResponseDTO(any(Medico.class))).thenReturn(new MedicoResponseDTO());
            assertNotNull(medicoService.actualizarMedicoHateoas(1L, medicoDTO));
            verify(medicoRepository).save(any(Medico.class));
        }
    }

    // --- Pruebas para listarMedicosConFranjas ---
    @Test
    void listarMedicosConFranjas_conDatosCompletos_mapeaCorrectamente() {
        when(medicoRepository.findAllWithDisponibilidades()).thenReturn(List.of(medico));

        List<MedicoFranjasDTO> resultado = medicoService.listarMedicosConFranjas();

        assertFalse(resultado.isEmpty());
        assertEquals(medico.getNombre(), resultado.get(0).getNombre());
    }

    // --- Pruebas de Ramas y Errores (para cobertura) ---

    @Test
    void crearMedico_cuandoEspecialidadNoExiste_lanzaExcepcion() {
        try (MockedStatic<MedicoMapper> mockedMapper = mockStatic(MedicoMapper.class)) {
            mockedMapper.when(() -> MedicoMapper.toEntity(medicoDTO)).thenReturn(medico);
            when(especialidadRepository.findById(anyLong())).thenReturn(Optional.empty());
            assertThrows(RuntimeException.class, () -> medicoService.crearMedico(medicoDTO));
        }
    }

    @Test
    void crearMedicoHateoas_cuandoRolNoExiste_lanzaExcepcion() {
        try (MockedStatic<MedicoMapper> mockedMapper = mockStatic(MedicoMapper.class)) {
            mockedMapper.when(() -> MedicoMapper.toEntity(medicoDTO)).thenReturn(medico);
            when(especialidadRepository.findById(anyLong())).thenReturn(Optional.of(especialidad));
            when(rolRepository.findById(anyLong())).thenReturn(Optional.empty());
            assertThrows(RuntimeException.class, () -> medicoService.crearMedicoHateoas(medicoDTO));
        }
    }

    @Test
    void actualizarMedicoHateoas_cuandoMedicoNoExiste_lanzaExcepcion() {
        when(medicoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> medicoService.actualizarMedicoHateoas(99L, medicoDTO));
    }

    @Test
    void actualizarMedicoHateoas_cuandoNuevaEspecialidadNoExiste_lanzaExcepcion() {
        medicoDTO.setEspecialidadId(99L);
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));
        when(especialidadRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> medicoService.actualizarMedicoHateoas(1L, medicoDTO));
    }

    @Test
    void eliminarMedico_cuandoNoExiste_lanzaExcepcion() {
        when(medicoRepository.existsById(99L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> medicoService.eliminarMedico(99L));
    }

    @Test
    void listarMedicosConFranjas_cuandoRelacionesSonNulas_mapeaCorrectamente() {
        medico.setEspecialidad(null);
        medico.setRolId(null);
        medico.setDisponibilidades(Collections.emptyList());
        when(medicoRepository.findAllWithDisponibilidades()).thenReturn(List.of(medico));

        List<MedicoFranjasDTO> resultado = medicoService.listarMedicosConFranjas();

        assertFalse(resultado.isEmpty());
        MedicoFranjasDTO dto = resultado.get(0);
        assertNull(dto.getEspecialidadNombre());
        assertNull(dto.getRolNombre());
    }
}