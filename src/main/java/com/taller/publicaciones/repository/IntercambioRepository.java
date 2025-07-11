package com.taller.publicaciones.repository;

import com.taller.publicaciones.model.Intercambio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntercambioRepository extends JpaRepository<Intercambio, Long> {
    
    // Obtener intercambios donde el usuario es propietario del producto solicitado en estado ACEPTADO o PROCESO
    @Query("SELECT i FROM Intercambio i WHERE i.idUsuarioPropietario = :userId AND (i.estadoIntercambio = 'ACEPTADO' OR i.estadoIntercambio = 'PROCESO' OR i.estadoIntercambio = 'PENDIENTE')")
    List<Intercambio> findOfertasRecibidas(@Param("userId") Long userId);
    
    // Obtener intercambios donde el usuario es solicitante en estado ACEPTADO o PROCESO
    @Query("SELECT i FROM Intercambio i WHERE i.idUsuarioSolicitante = :userId AND (i.estadoIntercambio = 'ACEPTADO' OR i.estadoIntercambio = 'PROCESO' OR i.estadoIntercambio = 'PENDIENTE')")
    List<Intercambio> findOfertasEnviadas(@Param("userId") Long userId);
    
    // Obtener intercambios pendientes para un producto específico
    @Query("SELECT i FROM Intercambio i WHERE i.productoSolicitado.id = :productoId AND i.estadoIntercambio = 'PENDIENTE'")
    List<Intercambio> findOfertasPendientesPorProducto(@Param("productoId") Long productoId);
    
    // Verificar si ya existe una oferta pendiente entre dos productos
    @Query("SELECT COUNT(i) > 0 FROM Intercambio i WHERE i.productoSolicitado.id = :productoSolicitadoId AND i.productoOfrecido.id = :productoOfrecidoId AND i.estadoIntercambio = 'PENDIENTE'")
    boolean existsOfertaPendiente(@Param("productoSolicitadoId") Long productoSolicitadoId, @Param("productoOfrecidoId") Long productoOfrecidoId);
} 