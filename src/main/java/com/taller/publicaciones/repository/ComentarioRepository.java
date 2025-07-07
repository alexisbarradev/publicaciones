package com.taller.publicaciones.repository;

import com.taller.publicaciones.model.Comentario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    List<Comentario> findByIdPublicacion(Long idPublicacion);
    
    Page<Comentario> findByIdPublicacion(Long idPublicacion, Pageable pageable);
    
    List<Comentario> findByIdAutor(Long idAutor);
    
    Page<Comentario> findByIdAutor(Long idAutor, Pageable pageable);
    
    @Query("SELECT c FROM Comentario c WHERE c.idPublicacion = :idPublicacion ORDER BY c.fechaCreacion ASC")
    List<Comentario> findByIdPublicacionOrderByFechaCreacionAsc(@Param("idPublicacion") Long idPublicacion);
    
    @Query("SELECT COUNT(c) FROM Comentario c WHERE c.idPublicacion = :idPublicacion")
    Long countByIdPublicacion(@Param("idPublicacion") Long idPublicacion);
} 