package com.citasalud.backend.security;

import com.citasalud.backend.domain.Medico; // Asumiendo que tu entidad Medico está aquí
import com.citasalud.backend.repository.MedicoRepository; // Asumiendo que tienes un repositorio para Medico
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private MedicoRepository medicoRepository;

    public CustomUserDetailsService(MedicoRepository medicoRepository) {
        this.medicoRepository = medicoRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Busca el médico por email (que será el "username" para el login)
        Medico medico = medicoRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Médico no encontrado con el email: " + email));

        // Convierte los roles del médico (asumiendo que Medico tiene un campo 'rol' o acceso a roles)
        // Por ahora, asumiremos que Medico tiene un campo 'rol' que es un String, ej. "MEDICO" o "COORDINADOR"
        // Si tu relación es con la entidad Rol, ajusta esto.
        List<String> roles = List.of(medico.getRolId().getNombre()); // Asumiendo medico.getRol().getNombre() devuelve el String del rol

        return new User(medico.getEmail(), medico.getPassword(), mapRolesToAuthorities(roles));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(List<String> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // ¡Aquí se añade el prefijo "ROLE_"!
                .collect(Collectors.toList());
    }
    // Helper para convertir roles de String a GrantedAuthority
    /*private Collection<? extends GrantedAuthority> mapRolesToAuthorities(List<String> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList());
    }*/
}
