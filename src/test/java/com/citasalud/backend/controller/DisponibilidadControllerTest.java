package com.citasalud.backend.controller;

import com.citasalud.backend.DataProvider;
import com.citasalud.backend.dto.DisponibilidadDTO;
import com.citasalud.backend.service.DisponibilidadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@WebMvcTest(DisponibilidadController.class)
@WithMockUser(roles = "MEDICO") // Simula un usuario autenticado para todos los tests
class DisponibilidadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DisponibilidadService disponibilidadService;

    // Mocks para satisfacer el contexto de seguridad
    @MockBean
    private com.citasalud.backend.security.JwtTokenProvider jwtTokenProvider;
    @MockBean
    private com.citasalud.backend.security.CustomUserDetailsService customUserDetailsService;

    private DisponibilidadDTO disponibilidadDTO;

    @BeforeEach
    void setUp() {
        disponibilidadDTO = DataProvider.crearDisponibilidadDTOValida();
        disponibilidadDTO.setDisponibilidadId(1L);
    }

    // --- Test para agregarFranja ---
    @Test
    void agregarFranja_conDatosValidos_debeRetornarCreated() throws Exception {
        when(disponibilidadService.agregarFranjaHateoas(any(DisponibilidadDTO.class), anyLong())).thenReturn(disponibilidadDTO);

        mockMvc.perform(post("/api/franjas/{medicoId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(disponibilidadDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.disponibilidadId", is(1)));
    }

    // --- Tests para obtenerDisponibilidadesPorMedico ---
    @Test
    void obtenerDisponibilidadesPorMedico_cuandoHayResultados_debeRetornarOkConLista() throws Exception {
        when(disponibilidadService.obtenerDisponibilidadesPorMedico(1L)).thenReturn(List.of(disponibilidadDTO));

        mockMvc.perform(get("/api/franjas/medico/{medicoId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.disponibilidadDTOList", hasSize(1)))
                .andExpect(jsonPath("$._embedded.disponibilidadDTOList[0].disponibilidadId", is(1)));
    }

    // --- Tests para obtenerFranjas (cubre línea roja) ---
    @Test
    void obtenerFranjas_cuandoHayResultados_debeRetornarOkConLista() throws Exception {
        when(disponibilidadService.listarFranjas()).thenReturn(List.of(disponibilidadDTO));

        mockMvc.perform(get("/api/franjas/listarfranjas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.disponibilidadDTOList", hasSize(1)));
    }

    // --- Tests para obtenerFranjaPorId (cubre línea roja) ---
    @Test
    void obtenerFranjaPorId_cuandoExiste_debeRetornarOk() throws Exception {
        when(disponibilidadService.obtenerFranjaPorIdHateoas(1L)).thenReturn(disponibilidadDTO);

        mockMvc.perform(get("/api/franjas/{idFranja}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.disponibilidadId", is(1)));
    }

    @Test
    void obtenerFranjaPorId_cuandoNoExiste_debeRetornarNotFound() throws Exception {
        when(disponibilidadService.obtenerFranjaPorIdHateoas(99L)).thenReturn(null);

        mockMvc.perform(get("/api/franjas/{idFranja}", 99L))
                .andExpect(status().isNotFound());
    }

    // --- Tests para actualizarFranja ---
    @Test
    void actualizarFranja_cuandoExiste_debeRetornarOk() throws Exception {
        when(disponibilidadService.actualizarFranjaHateoas(eq(1L), any(DisponibilidadDTO.class))).thenReturn(disponibilidadDTO);

        mockMvc.perform(put("/api/franjas/{idFranja}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(disponibilidadDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.disponibilidadId", is(1)));
    }

    // --- Test para eliminarFranja ---
    @Test
    void eliminarFranja_cuandoExiste_debeRetornarNoContent() throws Exception {
        doNothing().when(disponibilidadService).eliminarFranja(1L);

        mockMvc.perform(delete("/api/franjas/{idFranja}", 1L)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}