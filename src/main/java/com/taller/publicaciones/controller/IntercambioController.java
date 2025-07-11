package com.taller.publicaciones.controller;

import com.taller.publicaciones.model.*;
import com.taller.publicaciones.service.IntercambioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RestController
@RequestMapping("/api/intercambios")
@CrossOrigin(origins = {"http://localhost:4200", "https://3.135.134.201", "http://3.135.134.201"})
@Slf4j
public class IntercambioController {

    @Autowired
    private IntercambioService intercambioService;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        log.info("Endpoint de prueba de intercambios llamado");
        return ResponseEntity.ok("Intercambios API funcionando correctamente");
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearOfertaIntercambio(@RequestBody IntercambioDTO intercambioDTO) {
        log.info("Recibida solicitud de creación de intercambio: {}", intercambioDTO);
        try {
            Intercambio intercambio = intercambioService.crearOfertaIntercambio(intercambioDTO);
            log.info("Intercambio creado exitosamente con ID: {}", intercambio.getId());
            return ResponseEntity.ok(intercambio);
        } catch (RuntimeException e) {
            log.error("Error al crear intercambio: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/aceptar")
    public ResponseEntity<?> aceptarOferta(@PathVariable Long id) {
        log.info("Intentando aceptar oferta con ID: {}", id);
        try {
            Intercambio intercambio = intercambioService.aceptarOferta(id);
            log.info("Oferta aceptada exitosamente: {}", intercambio.getId());
            return ResponseEntity.ok(intercambio);
        } catch (RuntimeException e) {
            log.error("Error al aceptar oferta con ID {}: {}", id, e.getMessage());
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

    @PostMapping("/{id}/confirmar")
    public ResponseEntity<?> confirmarIntercambio(@PathVariable Long id, @RequestParam Long userId) {
        try {
            Intercambio intercambio = intercambioService.confirmarIntercambio(id, userId);
            return ResponseEntity.ok(intercambio);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/revertir")
    public ResponseEntity<?> revertirIntercambio(@PathVariable Long id, @RequestParam Long userId) {
        try {
            Intercambio intercambio = intercambioService.revertirIntercambio(id, userId);
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