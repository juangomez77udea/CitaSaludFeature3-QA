package com.citasalud.backend.controller;

import com.citasalud.backend.domain.Rol;
import com.citasalud.backend.repository.RolRepository;
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
@WebMvcTest(RolController.class)
class RolControllerTest {

    // 2. MockMvc para simular peticiones HTTP
    @Autowired
    private MockMvc mockMvc;

    // 3. Mock del repositorio que el controlador necesita
    @MockBean
    private RolRepository rolRepository;

    // 4. Mocks para satisfacer el contexto de seguridad global
    @MockBean
    private com.citasalud.backend.security.JwtTokenProvider jwtTokenProvider;
    @MockBean
    private com.citasalud.backend.security.CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser // Simula un usuario autenticado
    void listarRoles_cuandoHayDatos_debeRetornarListaConRoles() throws Exception {
        // GIVEN: Configuramos el mock para que devuelva una lista de roles
        Rol rolAdmin = new Rol();
        rolAdmin.setRolId(1L);
        rolAdmin.setNombre("ADMIN");

        Rol rolMedico = new Rol();
        rolMedico.setRolId(2L);
        rolMedico.setNombre("MEDICO");

        when(rolRepository.findAll()).thenReturn(List.of(rolAdmin, rolMedico));

        // WHEN & THEN: Hacemos la llamada y verificamos la respuesta
        mockMvc.perform(get("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2))) // Verifica que la lista tiene 2 elementos
                .andExpect(jsonPath("$[0].nombre", is("ADMIN")))
                .andExpect(jsonPath("$[1].rolId", is(2)));
    }

    @Test
    @WithMockUser
    void listarRoles_cuandoNoHayDatos_debeRetornarListaVacia() throws Exception {
        // GIVEN: Configuramos el mock para que devuelva una lista vacía
        when(rolRepository.findAll()).thenReturn(Collections.emptyList());

        // WHEN & THEN: Hacemos la llamada y verificamos la respuesta
        mockMvc.perform(get("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0))); // Verifica que la lista está vacía
    }
}