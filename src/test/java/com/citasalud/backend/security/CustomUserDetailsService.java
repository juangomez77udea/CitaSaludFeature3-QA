package com.citasalud.backend.security;

import com.citasalud.backend.domain.Medico;
import com.citasalud.backend.domain.Rol;
import com.citasalud.backend.repository.MedicoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private MedicoRepository medicoRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_cuandoUsuarioExiste_debeRetornarUserDetails() {
        // ARRANGE
        String email = "test@example.com";

        // 1. Crear el Rol de prueba
        Rol rolDePrueba = new Rol();
        rolDePrueba.setNombre("MEDICO");

        // 2. Crear el Medico de prueba
        Medico medicoDePrueba = new Medico();
        medicoDePrueba.setEmail(email);
        medicoDePrueba.setPassword("hashedpassword");
        medicoDePrueba.setRolId(rolDePrueba);

        // 3. Configurar el mock del repositorio para que devuelva nuestro médico
        when(medicoRepository.findByEmail(email)).thenReturn(Optional.of(medicoDePrueba));

        // ACT
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        // ASSERT
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals("hashedpassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_MEDICO")));
    }

    @Test
    void loadUserByUsername_cuandoUsuarioNoExiste_debeLanzarUsernameNotFoundException() {
        // ARRANGE
        String emailInexistente = "noexiste@example.com";

        // Configurar el mock para que devuelva un Optional vacío, simulando que no encontró al usuario
        when(medicoRepository.findByEmail(emailInexistente)).thenReturn(Optional.empty());

        // ACT & ASSERT
        // Verificamos que se lanza la excepción correcta
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(emailInexistente);
        });

        // Verificamos que el mensaje de la excepción es el esperado
        assertEquals("Médico no encontrado con el email: " + emailInexistente, exception.getMessage());
    }
}