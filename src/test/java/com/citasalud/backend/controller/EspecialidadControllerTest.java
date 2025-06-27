package com.citasalud.backend.controller;

import com.citasalud.backend.domain.Especialidad;
import com.citasalud.backend.repository.EspecialidadRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

// 1. Anotación para probar solo este controlador
@WebMvcTest(EspecialidadController.class)
class EspecialidadControllerTest {

    // 2. MockMvc para simular peticiones HTTP
    @Autowired
    private MockMvc mockMvc;

    // 3. Mock del repositorio que el controlador necesita
    @MockBean
    private EspecialidadRepository especialidadRepository;

    // 4. Mocks para satisfacer el contexto de seguridad (aunque este controlador no lo use directamente, es buena práctica)
    @MockBean
    private com.citasalud.backend.security.JwtTokenProvider jwtTokenProvider;
    @MockBean
    private com.citasalud.backend.security.CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser // Simula un usuario logueado para que la seguridad no bloquee la petición
    void listarEspecialidades_cuandoHayDatos_debeRetornarListaConEspecialidades() throws Exception {
        // GIVEN: Configuramos el mock para que devuelva una lista con dos especialidades
        Especialidad esp1 = new Especialidad();
        esp1.setEspecialidadId(1L);
        esp1.setEspecialidad("Cardiología");

        Especialidad esp2 = new Especialidad();
        esp2.setEspecialidadId(2L);
        esp2.setEspecialidad("Dermatología");

        when(especialidadRepository.findAll()).thenReturn(List.of(esp1, esp2));

        // WHEN & THEN: Hacemos la llamada y verificamos la respuesta
        mockMvc.perform(get("/api/especialidades")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2))) // Verifica que la lista tiene 2 elementos
                .andExpect(jsonPath("$[0].especialidad", is("Cardiología")))
                .andExpect(jsonPath("$[1].especialidadId", is(2)));
    }

    @Test
    @WithMockUser
    void listarEspecialidades_cuandoNoHayDatos_debeRetornarListaVacia() throws Exception {
        // GIVEN: Configuramos el mock para que devuelva una lista vacía
        when(especialidadRepository.findAll()).thenReturn(Collections.emptyList());

        // WHEN & THEN: Hacemos la llamada y verificamos la respuesta
        mockMvc.perform(get("/api/especialidades")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0))); // Verifica que la lista está vacía
    }
}