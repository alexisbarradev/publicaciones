package com.taller.publicaciones.controller;

import com.taller.publicaciones.model.Estado;
import com.taller.publicaciones.repository.EstadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/estados")
@RequiredArgsConstructor
public class EstadoController {

    private final EstadoRepository estadoRepository;

    @GetMapping
    public ResponseEntity<List<Estado>> getAllEstados() {
        return ResponseEntity.ok(estadoRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Estado> getEstadoById(@PathVariable Integer id) {
        Optional<Estado> estado = estadoRepository.findById(id);
        return estado.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 