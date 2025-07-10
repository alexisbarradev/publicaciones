package com.taller.publicaciones.service;

import com.taller.publicaciones.model.Publicacion;
import com.taller.publicaciones.model.PublicacionUpdateDTO;
import com.taller.publicaciones.repository.PublicacionRepository;
import com.taller.publicaciones.repository.EstadoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import com.taller.publicaciones.model.Estado;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PublicacionService {

    private final PublicacionRepository publicacionRepository;
    private final EstadoRepository estadoRepository;
    private final S3Service s3Service;

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

    public Publicacion update(Long id, PublicacionUpdateDTO publicacionDetails) {
        return publicacionRepository.findById(id)
                .map(publicacion -> {
                    // Only update fields that are provided (not null)
                    if (publicacionDetails.getTitulo() != null) {
                        publicacion.setTitulo(publicacionDetails.getTitulo());
                    }
                    if (publicacionDetails.getDescripcion() != null) {
                        publicacion.setDescripcion(publicacionDetails.getDescripcion());
                    }
                    if (publicacionDetails.getPrecio() != null) {
                        publicacion.setPrecio(publicacionDetails.getPrecio());
                    }
                    if (publicacionDetails.getUrlFoto() != null) {
                        publicacion.setUrlFoto(publicacionDetails.getUrlFoto());
                    }
                    
                    // Handle estado update
                    if (publicacionDetails.getEstado() != null && publicacionDetails.getEstado().getId() != null) {
                        publicacion.setEstado(
                            estadoRepository.findById(publicacionDetails.getEstado().getId())
                                .orElseThrow(() -> new RuntimeException("Estado no encontrado con id: " + publicacionDetails.getEstado().getId()))
                        );
                    }
                    
                    // No necesitamos manejar idAutor ya que el DTO no lo incluye
                    // El idAutor se preserva automáticamente del objeto existente
                    
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

    public List<Publicacion> findByEstadoPublicado() {
        return publicacionRepository.findByEstado_Id(1);
    }

    public List<Publicacion> findByIdAutorAndEstado(Long idAutor, Integer idEstado) {
        return publicacionRepository.findByIdAutorAndEstado_Id(idAutor, idEstado);
    }

    public Publicacion uploadFoto(Long publicacionId, MultipartFile file) throws IOException {
        Publicacion publicacion = publicacionRepository.findById(publicacionId)
                .orElseThrow(() -> new RuntimeException("Publicacion no encontrada con id: " + publicacionId));
        String urlFoto = s3Service.uploadFile(file);
        publicacion.setUrlFoto(urlFoto);
        return publicacionRepository.save(publicacion);
    }

    public Estado getEstadoById(Integer id) {
        return estadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado con id: " + id));
    }

    public String uploadFotoArchivo(MultipartFile file) throws IOException {
        return s3Service.uploadFile(file);
    }
} 