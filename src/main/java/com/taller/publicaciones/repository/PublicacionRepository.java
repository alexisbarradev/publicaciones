package com.taller.publicaciones.repository;

import com.taller.publicaciones.model.Publicacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {

    List<Publicacion> findByIdAutor(Long idAutor);
    
    Page<Publicacion> findByIdAutor(Long idAutor, Pageable pageable);
    
    @Query("SELECT p FROM Publicacion p WHERE p.titulo LIKE %:titulo% OR p.descripcion LIKE %:descripcion%")
    Page<Publicacion> findByTituloOrDescripcionContaining(
        @Param("titulo") String titulo, 
        @Param("descripcion") String descripcion, 
        Pageable pageable
    );
    
    @Query("SELECT p FROM Publicacion p ORDER BY p.fechaCreacion DESC")
    Page<Publicacion> findAllOrderByFechaCreacionDesc(Pageable pageable);
} 