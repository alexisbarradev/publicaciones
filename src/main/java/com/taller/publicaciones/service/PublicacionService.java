package com.taller.publicaciones.service;

import com.taller.publicaciones.model.Publicacion;
import com.taller.publicaciones.repository.PublicacionRepository;
import com.taller.publicaciones.repository.EstadoRepository;
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
    private final EstadoRepository estadoRepository;

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
        log.info("Creating new publication: {}", publicacion.getTitulo());
        if (publicacion.getEstado() != null && publicacion.getEstado().getId() != null) {
            publicacion.setEstado(
                estadoRepository.findById(publicacion.getEstado().getId())
                    .orElseThrow(() -> new RuntimeException("Estado no encontrado con id: " + publicacion.getEstado().getId()))
            );
        }
        return publicacionRepository.save(publicacion);
    }

    public Publicacion update(Long id, Publicacion publicacionDetails) {
        return publicacionRepository.findById(id)
                .map(publicacion -> {
                    publicacion.setTitulo(publicacionDetails.getTitulo());
                    publicacion.setDescripcion(publicacionDetails.getDescripcion());
                    if (publicacionDetails.getEstado() != null && publicacionDetails.getEstado().getId() != null) {
                        publicacion.setEstado(
                            estadoRepository.findById(publicacionDetails.getEstado().getId())
                                .orElseThrow(() -> new RuntimeException("Estado no encontrado con id: " + publicacionDetails.getEstado().getId()))
                        );
                    }
                    publicacion.setPrecio(publicacionDetails.getPrecio());
                    
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