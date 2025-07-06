package com.backend.trading.controller;

import com.backend.trading.model.Comentario;
import com.backend.trading.service.ComentarioService;
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
@RequestMapping("/api/comentarios")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ComentarioController {

    private final ComentarioService comentarioService;

    @GetMapping
    public ResponseEntity<List<Comentario>> getAllComentarios() {
        log.info("Getting all comments");
        List<Comentario> comentarios = comentarioService.findAll();
        return ResponseEntity.ok(comentarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comentario> getComentarioById(@PathVariable Long id) {
        log.info("Getting comment with ID: {}", id);
        Optional<Comentario> comentario = comentarioService.findById(id);
        return comentario.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/publicacion/{idPublicacion}")
    public ResponseEntity<List<Comentario>> getComentariosByPublicacion(@PathVariable Long idPublicacion) {
        log.info("Getting comments for publication: {}", idPublicacion);
        List<Comentario> comentarios = comentarioService.findByIdPublicacion(idPublicacion);
        return ResponseEntity.ok(comentarios);
    }

    @GetMapping("/publicacion/{idPublicacion}/page")
    public ResponseEntity<Page<Comentario>> getComentariosByPublicacionPaginated(
            @PathVariable Long idPublicacion,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting comments for publication: {}, page: {}, size: {}", idPublicacion, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Comentario> comentarios = comentarioService.findByIdPublicacion(idPublicacion, pageable);
        return ResponseEntity.ok(comentarios);
    }

    @GetMapping("/autor/{idAutor}")
    public ResponseEntity<List<Comentario>> getComentariosByAutor(@PathVariable Long idAutor) {
        log.info("Getting comments by author: {}", idAutor);
        List<Comentario> comentarios = comentarioService.findByIdAutor(idAutor);
        return ResponseEntity.ok(comentarios);
    }

    @GetMapping("/autor/{idAutor}/page")
    public ResponseEntity<Page<Comentario>> getComentariosByAutorPaginated(
            @PathVariable Long idAutor,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting comments by author: {}, page: {}, size: {}", idAutor, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Comentario> comentarios = comentarioService.findByIdAutor(idAutor, pageable);
        return ResponseEntity.ok(comentarios);
    }

    @GetMapping("/publicacion/{idPublicacion}/count")
    public ResponseEntity<Long> getComentariosCountByPublicacion(@PathVariable Long idPublicacion) {
        log.info("Getting comment count for publication: {}", idPublicacion);
        Long count = comentarioService.countByIdPublicacion(idPublicacion);
        return ResponseEntity.ok(count);
    }

    @PostMapping
    public ResponseEntity<Comentario> createComentario(@Valid @RequestBody Comentario comentario) {
        log.info("Creating new comment for publication: {}", comentario.getIdPublicacion());
        try {
            Comentario savedComentario = comentarioService.save(comentario);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedComentario);
        } catch (RuntimeException e) {
            log.error("Error creating comment: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comentario> updateComentario(
            @PathVariable Long id,
            @Valid @RequestBody Comentario comentarioDetails) {
        log.info("Updating comment with ID: {}", id);
        try {
            Comentario updatedComentario = comentarioService.update(id, comentarioDetails);
            return ResponseEntity.ok(updatedComentario);
        } catch (RuntimeException e) {
            log.error("Error updating comment: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComentario(@PathVariable Long id) {
        log.info("Deleting comment with ID: {}", id);
        try {
            comentarioService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error deleting comment: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
} 