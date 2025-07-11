package com.taller.publicaciones.controller;

import com.taller.publicaciones.model.Publicacion;
import com.taller.publicaciones.model.PublicacionUpdateDTO;
import com.taller.publicaciones.service.PublicacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/publicaciones")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PublicacionController {

    private final PublicacionService publicacionService;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        log.info("Endpoint de prueba de publicaciones llamado");
        return ResponseEntity.ok("Publicaciones API funcionando correctamente");
    }

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

    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<Publicacion>> getPublicacionesByUsuario(@PathVariable Long userId) {
        log.info("Getting publications by user: {}", userId);
        List<Publicacion> publicaciones = publicacionService.findByIdAutor(userId);
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

    @GetMapping("/publicados")
    public ResponseEntity<List<Publicacion>> getPublicacionesPublicadas() {
        List<Publicacion> publicaciones = publicacionService.findByEstadoPublicado();
        return ResponseEntity.ok(publicaciones);
    }

    @GetMapping("/autor/{idAutor}/estado/{idEstado}")
    public ResponseEntity<List<Publicacion>> getPublicacionesByAutorAndEstado(@PathVariable Long idAutor, @PathVariable Integer idEstado) {
        List<Publicacion> publicaciones = publicacionService.findByIdAutorAndEstado(idAutor, idEstado);
        return ResponseEntity.ok(publicaciones);
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Publicacion> createPublicacion(
            @RequestParam("titulo") String titulo,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("precio") Integer precio,
            @RequestParam("idAutor") Long idAutor,
            @RequestParam("estadoId") Integer estadoId,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            Publicacion publicacion = new Publicacion();
            publicacion.setTitulo(titulo);
            publicacion.setDescripcion(descripcion);
            publicacion.setPrecio(precio);
            publicacion.setIdAutor(idAutor);
            // Buscar y asignar el estado
            publicacion.setEstado(publicacionService.getEstadoById(estadoId));
            // Si hay foto, subirla y asignar la URL
            if (file != null && !file.isEmpty()) {
                String urlFoto = publicacionService.uploadFotoArchivo(file);
                publicacion.setUrlFoto(urlFoto);
            }
            Publicacion savedPublicacion = publicacionService.save(publicacion);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPublicacion);
        } catch (Exception e) {
            log.error("Error creating publication with photo: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/debug-content-type")
    public ResponseEntity<String> debugContentType(HttpServletRequest request) {
        return ResponseEntity.ok("Content-Type recibido: " + request.getContentType());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Publicacion> updatePublicacion(
            @PathVariable Long id,
            @RequestBody PublicacionUpdateDTO publicacionDetails) {
        log.info("Updating publication with ID: {}", id);
        try {
            Publicacion updatedPublicacion = publicacionService.update(id, publicacionDetails);
            return ResponseEntity.ok(updatedPublicacion);
        } catch (RuntimeException e) {
            log.error("Error updating publication: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/foto")
    public ResponseEntity<Publicacion> uploadFoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            Publicacion publicacion = publicacionService.uploadFoto(id, file);
            return ResponseEntity.ok(publicacion);
        } catch (Exception e) {
            log.error("Error uploading photo for publication {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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