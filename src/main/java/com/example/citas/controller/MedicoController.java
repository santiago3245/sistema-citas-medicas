package com.example.citas.controller;

import com.example.citas.dto.MedicoDTO;
import com.example.citas.service.MedicoServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicos")
public class MedicoController {

    private final MedicoServicio medicoServicio;

    public MedicoController(MedicoServicio medicoServicio) {
        this.medicoServicio = medicoServicio;
    }

    @GetMapping
    public ResponseEntity<List<MedicoDTO>> obtenerTodos() {
        return ResponseEntity.ok(medicoServicio.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicoDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(medicoServicio.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<MedicoDTO> crear(@RequestBody MedicoDTO dto) {
        return ResponseEntity.ok(medicoServicio.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicoDTO> modificar(@PathVariable Long id, @RequestBody MedicoDTO dto) {
        return ResponseEntity.ok(medicoServicio.modificar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        medicoServicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
