package com.example.citas.service;

import com.example.citas.dto.CitaDTO;
import com.example.citas.model.Cita;
import com.example.citas.model.Consultorio;
import com.example.citas.model.Medico;
import com.example.citas.model.Paciente;
import com.example.citas.repository.CitaRepository;
import com.example.citas.repository.ConsultorioRepository;
import com.example.citas.repository.MedicoRepository;
import com.example.citas.repository.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CitaServicio {

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final ConsultorioRepository consultorioRepository;

    public CitaServicio(CitaRepository citaRepository,
                        PacienteRepository pacienteRepository,
                        MedicoRepository medicoRepository,
                        ConsultorioRepository consultorioRepository) {
        this.citaRepository = citaRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.consultorioRepository = consultorioRepository;
    }

    public List<CitaDTO> obtenerTodas() {
        return citaRepository.findAll()
                .stream()
                .map(this::aDTO)
                .collect(Collectors.toList());
    }

    public CitaDTO obtenerPorId(Long id) {
        Cita c = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        return aDTO(c);
    }

    @Transactional
    public CitaDTO crear(CitaDTO dto) {
        // Validaciones de existencia
        Paciente p = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente no existe"));
        Medico m = medicoRepository.findById(dto.getMedicoId())
                .orElseThrow(() -> new RuntimeException("Médico no existe"));
        Consultorio cons = consultorioRepository.findById(dto.getConsultorioId())
                .orElseThrow(() -> new RuntimeException("Consultorio no existe"));

        // Regla de negocio: un médico no puede tener dos citas en el mismo horario
        citaRepository.findByMedicoIdAndFechaAndHora(dto.getMedicoId(), dto.getFecha(), dto.getHora())
                .ifPresent(x -> { throw new RuntimeException("El médico ya tiene una cita en ese horario"); });

        // Crear y guardar
        Cita c = new Cita();
        c.setPaciente(p);
        c.setMedico(m);
        c.setConsultorio(cons);
        c.setFecha(dto.getFecha());
        c.setHora(dto.getHora());

        citaRepository.save(c);
        return aDTO(c);
    }

    @Transactional
    public CitaDTO modificar(Long id, CitaDTO dto) {
        Cita c = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        // Si cambian IDs relacionados, validamos nuevas referencias
        if (dto.getPacienteId() != null && (c.getPaciente() == null || !dto.getPacienteId().equals(c.getPaciente().getId()))) {
            Paciente p = pacienteRepository.findById(dto.getPacienteId())
                    .orElseThrow(() -> new RuntimeException("Paciente no existe"));
            c.setPaciente(p);
        }

        if (dto.getMedicoId() != null && (c.getMedico() == null || !dto.getMedicoId().equals(c.getMedico().getId()))) {
            Medico m = medicoRepository.findById(dto.getMedicoId())
                    .orElseThrow(() -> new RuntimeException("Médico no existe"));
            c.setMedico(m);
        }

        if (dto.getConsultorioId() != null && (c.getConsultorio() == null || !dto.getConsultorioId().equals(c.getConsultorio().getId()))) {
            Consultorio cons = consultorioRepository.findById(dto.getConsultorioId())
                    .orElseThrow(() -> new RuntimeException("Consultorio no existe"));
            c.setConsultorio(cons);
        }

        // Actualizar fecha/hora y validar solapamiento si cambian
        boolean cambiaHorario = false;

        if (dto.getFecha() != null && (c.getFecha() == null || !dto.getFecha().equals(c.getFecha()))) {
            c.setFecha(dto.getFecha());
            cambiaHorario = true;
        }

        if (dto.getHora() != null && (c.getHora() == null || !dto.getHora().equals(c.getHora()))) {
            c.setHora(dto.getHora());
            cambiaHorario = true;
        }

        if (cambiaHorario && c.getMedico() != null) {
            // Verificar conflicto para el médico en el nuevo horario (excluyendo esta misma cita)
            citaRepository.findByMedicoIdAndFechaAndHora(c.getMedico().getId(), c.getFecha(), c.getHora())
                    .ifPresent(conf -> {
                        if (!conf.getId().equals(c.getId())) {
                            throw new RuntimeException("El médico ya tiene una cita en ese horario");
                        }
                    });
        }

        citaRepository.save(c);
        return aDTO(c);
    }

    public void eliminar(Long id) {
        citaRepository.deleteById(id);
    }

    private CitaDTO aDTO(Cita c) {
        return new CitaDTO(
                c.getId(),
                c.getPaciente() != null ? c.getPaciente().getId() : null,
                c.getMedico() != null ? c.getMedico().getId() : null,
                c.getConsultorio() != null ? c.getConsultorio().getId() : null,
                c.getFecha(),
                c.getHora()
        );
    }
}
