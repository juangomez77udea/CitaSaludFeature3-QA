// Ubicación: src/test/java/com/citasalud/backend/DataProvider.java
package com.citasalud.backend;

import com.citasalud.backend.domain.Disponibilidad;
import com.citasalud.backend.domain.Especialidad;
import com.citasalud.backend.domain.Medico;
import com.citasalud.backend.domain.Rol;
import com.citasalud.backend.dto.DisponibilidadDTO;
import com.citasalud.backend.dto.MedicoDTO;
import com.citasalud.backend.dto.MedicoFranjasDTO;
import com.citasalud.backend.dto.MedicoResponseDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DataProvider {

    // --- Métodos para MEDICO ---
    public static MedicoDTO crearMedicoDTO() {
        return new MedicoDTO("Carlos", "Santana", "carlos.santana@example.com", "supersecretpassword", "CC", "12345678", 1L, 2L);
    }

    public static List<MedicoResponseDTO> crearListaMedicosResponseDTO() {
        MedicoResponseDTO medico1 = new MedicoResponseDTO(1L, "Carlos", "Santana", "carlos.santana@example.com", "CC", "12345678", 1L, "Cardiología", 2L, "MÉDICO");
        MedicoResponseDTO medico2 = new MedicoResponseDTO(2L, "Laura", "Pausini", "laura.pausini@example.com", "CE", "11223344", 2L, "Dermatología", 2L, "MÉDICO");
        return List.of(medico1, medico2);
    }

    public static MedicoFranjasDTO crearMedicoConFranjasDTO() {
        return new MedicoFranjasDTO(101L, "Julio", "Iglesias", "julio.iglesias@example.com", "CC", "99887766", 5L, "Medicina General", 2L, "MÉDICO", new ArrayList<>());
    }

    public static List<MedicoFranjasDTO> crearListaMedicosConFranjasDTO() {
        MedicoFranjasDTO medico1 = crearMedicoConFranjasDTO();
        MedicoFranjasDTO medico2 = new MedicoFranjasDTO(102L, "Rocio", "Durcal", "rocio.durcal@example.com", "CE", "11223344", 6L, "Dermatología", 2L, "MÉDICO", new ArrayList<>());
        return List.of(medico1, medico2);
    }

    public static List<Medico> crearListaMedicosEntidad() {
        Medico medico1 = new Medico();
        medico1.setId(1L);
        medico1.setNombre("Juan");
        medico1.setApellido("Perez");
        medico1.setEmail("juan.perez@example.com");

        Medico medico2 = new Medico();
        medico2.setId(2L);
        medico2.setNombre("Ana");
        medico2.setApellido("Gomez");
        medico2.setEmail("ana.gomez@example.com");

        return List.of(medico1, medico2);
    }

    public static List<Medico> crearListaMedicosConRelaciones() {
        Especialidad cardiologia = new Especialidad();
        cardiologia.setEspecialidadId(1L);
        cardiologia.setEspecialidad("Cardiología");
        Rol rolMedico = new Rol();
        rolMedico.setRolId(2L);
        rolMedico.setNombre("MÉDICO");
        Disponibilidad franja1 = new Disponibilidad();
        Disponibilidad franja2 = new Disponibilidad();
        Medico medicoCompleto = new Medico();
        medicoCompleto.setId(1L);
        medicoCompleto.setNombre("Doctor");
        medicoCompleto.setApellido("House");
        medicoCompleto.setEmail("house@example.com");
        medicoCompleto.setEspecialidad(cardiologia);
        medicoCompleto.setRolId(rolMedico);
        medicoCompleto.setDisponibilidades(List.of(franja1, franja2));
        return List.of(medicoCompleto);
    }

    public static List<Medico> crearMedicoConDisponibilidadesNulas() {
        Especialidad cardiologia = new Especialidad();
        cardiologia.setEspecialidadId(1L);
        cardiologia.setEspecialidad("Cardiología");
        Rol rolMedico = new Rol();
        rolMedico.setRolId(2L);
        rolMedico.setNombre("MÉDICO");
        Medico medicoSinFranjas = new Medico();
        medicoSinFranjas.setId(3L);
        medicoSinFranjas.setNombre("Doctora");
        medicoSinFranjas.setApellido("Quinn");
        medicoSinFranjas.setEmail("quinn@example.com");
        medicoSinFranjas.setEspecialidad(cardiologia);
        medicoSinFranjas.setRolId(rolMedico);
        medicoSinFranjas.setDisponibilidades(null);
        return List.of(medicoSinFranjas);
    }

    // --- Métodos para DISPONIBILIDAD (CORREGIDOS) ---
    public static DisponibilidadDTO crearDisponibilidadDTOValida() {
        // CORREGIDO: Se añade el medicoId (1L) como último argumento
        return new DisponibilidadDTO(null, List.of("LUNES", "MIERCOLES"), LocalDate.now().plusDays(1), LocalDate.now().plusMonths(3), LocalTime.of(9, 0), LocalTime.of(13, 0), 1L);
    }

    public static DisponibilidadDTO crearDisponibilidadDTOInvalida() {
        // CORREGIDO: Se añade el medicoId (1L) como último argumento
        return new DisponibilidadDTO(null, new ArrayList<>(), LocalDate.now().plusDays(1), LocalDate.now().plusMonths(3), LocalTime.of(14, 0), LocalTime.of(18, 0), 1L);
    }

    public static List<DisponibilidadDTO> crearListaDisponibilidadDTO() {
        // CORREGIDO: Se añade el medicoId a cada constructor
        DisponibilidadDTO franjaManana = new DisponibilidadDTO(1L, List.of("LUNES", "MARTES"), LocalDate.now().plusDays(1), LocalDate.now().plusMonths(1), LocalTime.of(8, 0), LocalTime.of(12, 0), 1L);
        DisponibilidadDTO franjaTarde = new DisponibilidadDTO(2L, List.of("MIERCOLES", "JUEVES"), LocalDate.now().plusDays(2), LocalDate.now().plusMonths(2), LocalTime.of(14, 0), LocalTime.of(18, 0), 2L);
        return List.of(franjaManana, franjaTarde);
    }

    public static List<Disponibilidad> crearListaDisponibilidadEntidad() {
        Disponibilidad franja1 = new Disponibilidad();
        franja1.setDisponibilidadId(1L);
        franja1.setDias(List.of("LUNES"));
        franja1.setHoraInicio(LocalTime.of(8, 0));

        Disponibilidad franja2 = new Disponibilidad();
        franja2.setDisponibilidadId(2L);
        franja2.setDias(List.of("MARTES", "JUEVES"));
        franja2.setHoraInicio(LocalTime.of(15, 0));

        return List.of(franja1, franja2);
    }
}