package com.backend.trading.controller;

import com.backend.trading.model.Publicacion;
import com.backend.trading.service.PublicacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/publicaciones")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PublicacionController {

    private final PublicacionService publicacionService;

    @GetMapping
    public ResponseEntity<List<Publicacion>> getAllPublicaciones() {
        log.info("Getting all publications");
        List<Publicacion> publicaciones = publicacionService.findAll();
        return ResponseEntity.ok(publicaciones);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<Publicacion>> getAllPublicacionesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting publications page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Publicacion> publicaciones = publicacionService.findAll(pageable);
        return ResponseEntity.ok(publicaciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Publicacion> getPublicacionById(@PathVariable Long id) {
        log.info("Getting publication with ID: {}", id);
        Optional<Publicacion> publicacion = publicacionService.findById(id);
        return publicacion.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/autor/{idAutor}")
    public ResponseEntity<List<Publicacion>> getPublicacionesByAutor(@PathVariable Long idAutor) {
        log.info("Getting publications by author: {}", idAutor);
        List<Publicacion> publicaciones = publicacionService.findByIdAutor(idAutor);
        return ResponseEntity.ok(publicaciones);
    }

    @GetMapping("/autor/{idAutor}/page")
    public ResponseEntity<Page<Publicacion>> getPublicacionesByAutorPaginated(
            @PathVariable Long idAutor,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting publications by author: {}, page: {}, size: {}", idAutor, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Publicacion> publicaciones = publicacionService.findByIdAutor(idAutor, pageable);
        return ResponseEntity.ok(publicaciones);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Publicacion>> searchPublicaciones(
            @RequestParam String titulo,
            @RequestParam String descripcion,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Searching publications with title: {}, description: {}", titulo, descripcion);
        Pageable pageable = PageRequest.of(page, size);
        Page<Publicacion> publicaciones = publicacionService.findByTituloOrDescripcionContaining(titulo, descripcion, pageable);
        return ResponseEntity.ok(publicaciones);
    }

    @PostMapping
    public ResponseEntity<Publicacion> createPublicacion(@Valid @RequestBody Publicacion publicacion) {
        log.info("Creating new publication: {}", publicacion.getTitulo());
        try {
            Publicacion savedPublicacion = publicacionService.save(publicacion);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPublicacion);
        } catch (RuntimeException e) {
            log.error("Error creating publication: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Publicacion> updatePublicacion(
            @PathVariable Long id,
            @Valid @RequestBody Publicacion publicacionDetails) {
        log.info("Updating publication with ID: {}", id);
        try {
            Publicacion updatedPublicacion = publicacionService.update(id, publicacionDetails);
            return ResponseEntity.ok(updatedPublicacion);
        } catch (RuntimeException e) {
            log.error("Error updating publication: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePublicacion(@PathVariable Long id) {
        log.info("Deleting publication with ID: {}", id);
        try {
            publicacionService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error deleting publication: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
} 