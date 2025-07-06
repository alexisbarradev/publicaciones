package com.backend.trading.service;

import com.backend.trading.model.Publicacion;
import com.backend.trading.repository.PublicacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PublicacionService {

    private final PublicacionRepository publicacionRepository;
    private final UserServiceClient userServiceClient;

    public List<Publicacion> findAll() {
        return publicacionRepository.findAll();
    }

    public Page<Publicacion> findAll(Pageable pageable) {
        return publicacionRepository.findAllOrderByFechaCreacionDesc(pageable);
    }

    public Optional<Publicacion> findById(Long id) {
        return publicacionRepository.findById(id);
    }

    public List<Publicacion> findByIdAutor(Long idAutor) {
        return publicacionRepository.findByIdAutor(idAutor);
    }

    public Page<Publicacion> findByIdAutor(Long idAutor, Pageable pageable) {
        return publicacionRepository.findByIdAutor(idAutor, pageable);
    }

    public Page<Publicacion> findByTituloOrDescripcionContaining(String titulo, String descripcion, Pageable pageable) {
        return publicacionRepository.findByTituloOrDescripcionContaining(titulo, descripcion, pageable);
    }

    public Publicacion save(Publicacion publicacion) {
        // Validate that the author exists in the user service
        userServiceClient.userExists(publicacion.getIdAutor())
                .subscribe(exists -> {
                    if (!exists) {
                        log.warn("Attempted to create publication with non-existent user ID: {}", publicacion.getIdAutor());
                        throw new RuntimeException("User with ID " + publicacion.getIdAutor() + " does not exist");
                    }
                });

        log.info("Creating new publication: {}", publicacion.getTitulo());
        return publicacionRepository.save(publicacion);
    }

    public Publicacion update(Long id, Publicacion publicacionDetails) {
        return publicacionRepository.findById(id)
                .map(publicacion -> {
                    publicacion.setTitulo(publicacionDetails.getTitulo());
                    publicacion.setDescripcion(publicacionDetails.getDescripcion());
                    publicacion.setIdEstadoCategoria(publicacionDetails.getIdEstadoCategoria());
                    
                    log.info("Updating publication with ID: {}", id);
                    return publicacionRepository.save(publicacion);
                })
                .orElseThrow(() -> new RuntimeException("Publicacion not found with id: " + id));
    }

    public void deleteById(Long id) {
        if (publicacionRepository.existsById(id)) {
            log.info("Deleting publication with ID: {}", id);
            publicacionRepository.deleteById(id);
        } else {
            throw new RuntimeException("Publicacion not found with id: " + id);
        }
    }

    public boolean existsById(Long id) {
        return publicacionRepository.existsById(id);
    }
} 