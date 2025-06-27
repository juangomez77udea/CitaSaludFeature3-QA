package com.citasalud.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 1. Anotación para probar solo este controlador
@WebMvcTest(TestController.class)
class TestControllerTest {

    // 2. MockMvc para simular peticiones HTTP
    @Autowired
    private MockMvc mockMvc;

    // 3. Mocks para satisfacer el contexto de seguridad global (aunque no se usen, son necesarios para que el contexto de prueba arranque)
    @MockBean
    private com.citasalud.backend.security.JwtTokenProvider jwtTokenProvider;
    @MockBean
    private com.citasalud.backend.security.CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser // Simula un usuario logueado para pasar los filtros de seguridad
    void hola_debeRetornarMensajeCorrecto() throws Exception {
        // GIVEN: No hay configuración necesaria ya que el endpoint no tiene dependencias.
        String mensajeEsperado = "¡Hola desde el backend!";

        // WHEN & THEN: Hacemos la llamada y verificamos la respuesta
        mockMvc.perform(get("/hola"))
                .andExpect(status().isOk()) // Verifica que el código de estado es 200
                .andExpect(content().string(mensajeEsperado)); // Verifica que el cuerpo de la respuesta es exactamente el string esperado
    }
}