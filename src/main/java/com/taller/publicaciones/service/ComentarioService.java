package com.backend.trading.service;

import com.backend.trading.model.Comentario;
import com.backend.trading.repository.ComentarioRepository;
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
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final PublicacionService publicacionService;
    private final UserServiceClient userServiceClient;

    public List<Comentario> findAll() {
        return comentarioRepository.findAll();
    }

    public Optional<Comentario> findById(Long id) {
        return comentarioRepository.findById(id);
    }

    public List<Comentario> findByIdPublicacion(Long idPublicacion) {
        return comentarioRepository.findByIdPublicacionOrderByFechaCreacionAsc(idPublicacion);
    }

    public Page<Comentario> findByIdPublicacion(Long idPublicacion, Pageable pageable) {
        return comentarioRepository.findByIdPublicacion(idPublicacion, pageable);
    }

    public List<Comentario> findByIdAutor(Long idAutor) {
        return comentarioRepository.findByIdAutor(idAutor);
    }

    public Page<Comentario> findByIdAutor(Long idAutor, Pageable pageable) {
        return comentarioRepository.findByIdAutor(idAutor, pageable);
    }

    public Long countByIdPublicacion(Long idPublicacion) {
        return comentarioRepository.countByIdPublicacion(idPublicacion);
    }

    public Comentario save(Comentario comentario) {
        // Validate that the publication exists
        if (!publicacionService.existsById(comentario.getIdPublicacion())) {
            throw new RuntimeException("Publicacion not found with id: " + comentario.getIdPublicacion());
        }

        // Validate that the author exists in the user service
        userServiceClient.userExists(comentario.getIdAutor())
                .subscribe(exists -> {
                    if (!exists) {
                        log.warn("Attempted to create comment with non-existent user ID: {}", comentario.getIdAutor());
                        throw new RuntimeException("User with ID " + comentario.getIdAutor() + " does not exist");
                    }
                });

        log.info("Creating new comment for publication: {}", comentario.getIdPublicacion());
        return comentarioRepository.save(comentario);
    }

    public Comentario update(Long id, Comentario comentarioDetails) {
        return comentarioRepository.findById(id)
                .map(comentario -> {
                    comentario.setTexto(comentarioDetails.getTexto());
                    comentario.setImagenUrl(comentarioDetails.getImagenUrl());
                    
                    log.info("Updating comment with ID: {}", id);
                    return comentarioRepository.save(comentario);
                })
                .orElseThrow(() -> new RuntimeException("Comentario not found with id: " + id));
    }

    public void deleteById(Long id) {
        if (comentarioRepository.existsById(id)) {
            log.info("Deleting comment with ID: {}", id);
            comentarioRepository.deleteById(id);
        } else {
            throw new RuntimeException("Comentario not found with id: " + id);
        }
    }

    public boolean existsById(Long id) {
        return comentarioRepository.existsById(id);
    }
} 