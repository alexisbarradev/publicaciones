package com.taller.publicaciones.controller;

import com.taller.publicaciones.model.*;
import com.taller.publicaciones.service.IntercambioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/intercambios")
@CrossOrigin(origins = "*")
public class IntercambioController {

    @Autowired
    private IntercambioService intercambioService;

    @PostMapping("/crear")
    public ResponseEntity<?> crearOfertaIntercambio(@RequestBody IntercambioDTO intercambioDTO) {
        try {
            Intercambio intercambio = intercambioService.crearOfertaIntercambio(intercambioDTO);
            return ResponseEntity.ok(intercambio);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/aceptar")
    public ResponseEntity<?> aceptarOferta(@PathVariable Long id) {
        try {
            Intercambio intercambio = intercambioService.aceptarOferta(id);
            return ResponseEntity.ok(intercambio);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/rechazar")
    public ResponseEntity<?> rechazarOferta(@PathVariable Long id) {
        try {
            Intercambio intercambio = intercambioService.rechazarOferta(id);
            return ResponseEntity.ok(intercambio);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/ofertas-recibidas/{userId}")
    public ResponseEntity<List<IntercambioResponseDTO>> getOfertasRecibidas(@PathVariable Long userId) {
        List<IntercambioResponseDTO> ofertas = intercambioService.getOfertasRecibidas(userId);
        return ResponseEntity.ok(ofertas);
    }

    @GetMapping("/ofertas-enviadas/{userId}")
    public ResponseEntity<List<IntercambioResponseDTO>> getOfertasEnviadas(@PathVariable Long userId) {
        List<IntercambioResponseDTO> ofertas = intercambioService.getOfertasEnviadas(userId);
        return ResponseEntity.ok(ofertas);
    }
} 