package com.taller.publicaciones.service;

import com.taller.publicaciones.model.*;
import com.taller.publicaciones.repository.IntercambioRepository;
import com.taller.publicaciones.repository.PublicacionRepository;
import com.taller.publicaciones.repository.EstadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IntercambioService {

    @Autowired
    private IntercambioRepository intercambioRepository;

    @Autowired
    private PublicacionRepository publicacionRepository;

    @Autowired
    private EstadoRepository estadoRepository;

    @Transactional
    public Intercambio crearOfertaIntercambio(IntercambioDTO intercambioDTO) {
        log.info("Iniciando creación de oferta de intercambio: {}", intercambioDTO);
        
        // Validar que no exista una oferta pendiente entre estos productos
        if (intercambioRepository.existsOfertaPendiente(
                intercambioDTO.getIdProductoSolicitado(), 
                intercambioDTO.getIdProductoOfrecido())) {
            log.warn("Ya existe una oferta pendiente entre los productos {} y {}", 
                    intercambioDTO.getIdProductoSolicitado(), intercambioDTO.getIdProductoOfrecido());
            throw new RuntimeException("Ya existe una oferta pendiente entre estos productos");
        }

        // Obtener las publicaciones
        Publicacion productoSolicitado = publicacionRepository.findById(intercambioDTO.getIdProductoSolicitado())
                .orElseThrow(() -> new RuntimeException("Producto solicitado no encontrado"));
        
        Publicacion productoOfrecido = publicacionRepository.findById(intercambioDTO.getIdProductoOfrecido())
                .orElseThrow(() -> new RuntimeException("Producto ofrecido no encontrado"));

        // Validar que el usuario solicitante sea el dueño del producto ofrecido
        if (!productoOfrecido.getIdAutor().equals(intercambioDTO.getIdUsuarioSolicitante())) {
            throw new RuntimeException("Solo puedes ofrecer tus propios productos");
        }

        // Validar que el usuario solicitante no sea el dueño del producto solicitado
        if (productoSolicitado.getIdAutor().equals(intercambioDTO.getIdUsuarioSolicitante())) {
            throw new RuntimeException("No puedes intercambiar tu propio producto");
        }

        // Validar que ambos productos estén en estado "Publicado"
        Estado estadoPublicado = estadoRepository.findById(1).orElse(null);
        if (!productoSolicitado.getEstado().equals(estadoPublicado) || 
            !productoOfrecido.getEstado().equals(estadoPublicado)) {
            throw new RuntimeException("Ambos productos deben estar en estado 'Publicado'");
        }

        Intercambio intercambio = new Intercambio();
        intercambio.setProductoSolicitado(productoSolicitado);
        intercambio.setProductoOfrecido(productoOfrecido);
        intercambio.setIdUsuarioSolicitante(intercambioDTO.getIdUsuarioSolicitante());
        intercambio.setIdUsuarioPropietario(intercambioDTO.getIdUsuarioPropietario());
        intercambio.setEstadoIntercambio(Intercambio.EstadoIntercambio.PENDIENTE);

        return intercambioRepository.save(intercambio);
    }

    @Transactional
    public Intercambio aceptarOferta(Long intercambioId) {
        Intercambio intercambio = intercambioRepository.findById(intercambioId)
                .orElseThrow(() -> new RuntimeException("Intercambio no encontrado"));

        if (intercambio.getEstadoIntercambio() != Intercambio.EstadoIntercambio.PENDIENTE) {
            throw new RuntimeException("Solo se pueden aceptar ofertas pendientes");
        }

        // Obtener el estado "Proceso"
        Estado estadoProceso = estadoRepository.findById(5).orElse(null);
        if (estadoProceso == null) {
            throw new RuntimeException("Estado 'Proceso' no encontrado");
        }

        // Cambiar estado de ambos productos a "Proceso"
        intercambio.getProductoSolicitado().setEstado(estadoProceso);
        intercambio.getProductoOfrecido().setEstado(estadoProceso);
        
        publicacionRepository.save(intercambio.getProductoSolicitado());
        publicacionRepository.save(intercambio.getProductoOfrecido());

        // Marcar este intercambio como aceptado
        intercambio.setEstadoIntercambio(Intercambio.EstadoIntercambio.ACEPTADO);
        intercambio.setFechaRespuesta(LocalDateTime.now());

        // Rechazar todas las demás ofertas pendientes para el producto solicitado
        List<Intercambio> otrasOfertas = intercambioRepository.findOfertasPendientesPorProducto(
                intercambio.getProductoSolicitado().getId());
        
        for (Intercambio otraOferta : otrasOfertas) {
            if (!otraOferta.getId().equals(intercambioId)) {
                otraOferta.setEstadoIntercambio(Intercambio.EstadoIntercambio.RECHAZADO);
                otraOferta.setFechaRespuesta(LocalDateTime.now());
                intercambioRepository.save(otraOferta);
            }
        }

        return intercambioRepository.save(intercambio);
    }

    @Transactional
    public Intercambio rechazarOferta(Long intercambioId) {
        Intercambio intercambio = intercambioRepository.findById(intercambioId)
                .orElseThrow(() -> new RuntimeException("Intercambio no encontrado"));

        if (intercambio.getEstadoIntercambio() != Intercambio.EstadoIntercambio.PENDIENTE) {
            throw new RuntimeException("Solo se pueden rechazar ofertas pendientes");
        }

        intercambio.setEstadoIntercambio(Intercambio.EstadoIntercambio.RECHAZADO);
        intercambio.setFechaRespuesta(LocalDateTime.now());

        return intercambioRepository.save(intercambio);
    }

    @Transactional
    public Intercambio confirmarIntercambio(Long intercambioId, Long userId) {
        Intercambio intercambio = intercambioRepository.findById(intercambioId)
                .orElseThrow(() -> new RuntimeException("Intercambio no encontrado"));

        // Solo se puede confirmar si el intercambio está en proceso
        if (intercambio.getEstadoIntercambio() != Intercambio.EstadoIntercambio.ACEPTADO) {
            throw new RuntimeException("Solo se pueden confirmar intercambios aceptados");
        }

        // Actualizar confirmación según el usuario
        if (userId.equals(intercambio.getIdUsuarioSolicitante())) {
            intercambio.setConfirmacionSolicitante(Intercambio.ConfirmacionEstado.CONFIRMADO);
        } else if (userId.equals(intercambio.getIdUsuarioPropietario())) {
            intercambio.setConfirmacionPropietario(Intercambio.ConfirmacionEstado.CONFIRMADO);
        } else {
            throw new RuntimeException("Solo los usuarios involucrados pueden confirmar");
        }

        // Si ambos confirman, pasar productos a 'Aprobado'
        if (intercambio.getConfirmacionSolicitante() == Intercambio.ConfirmacionEstado.CONFIRMADO &&
            intercambio.getConfirmacionPropietario() == Intercambio.ConfirmacionEstado.CONFIRMADO) {
            Estado estadoAprobado = estadoRepository.findById(4).orElse(null); // 4 = Aprobado
            if (estadoAprobado != null) {
                intercambio.getProductoSolicitado().setEstado(estadoAprobado);
                intercambio.getProductoOfrecido().setEstado(estadoAprobado);
                publicacionRepository.save(intercambio.getProductoSolicitado());
                publicacionRepository.save(intercambio.getProductoOfrecido());
            }
        }

        return intercambioRepository.save(intercambio);
    }

    @Transactional
    public Intercambio revertirIntercambio(Long intercambioId, Long userId) {
        Intercambio intercambio = intercambioRepository.findById(intercambioId)
                .orElseThrow(() -> new RuntimeException("Intercambio no encontrado"));

        // Solo se puede revertir si el intercambio está en proceso
        if (intercambio.getEstadoIntercambio() != Intercambio.EstadoIntercambio.ACEPTADO) {
            throw new RuntimeException("Solo se pueden revertir intercambios aceptados");
        }

        // Actualizar reversión según el usuario
        if (userId.equals(intercambio.getIdUsuarioSolicitante())) {
            intercambio.setConfirmacionSolicitante(Intercambio.ConfirmacionEstado.REVERTIDO);
        } else if (userId.equals(intercambio.getIdUsuarioPropietario())) {
            intercambio.setConfirmacionPropietario(Intercambio.ConfirmacionEstado.REVERTIDO);
        } else {
            throw new RuntimeException("Solo los usuarios involucrados pueden revertir");
        }

        // Si cualquiera revierte, ambos productos vuelven a 'Publicado' y el intercambio se cancela
        if (intercambio.getConfirmacionSolicitante() == Intercambio.ConfirmacionEstado.REVERTIDO ||
            intercambio.getConfirmacionPropietario() == Intercambio.ConfirmacionEstado.REVERTIDO) {
            Estado estadoPublicado = estadoRepository.findById(1).orElse(null); // 1 = Publicado
            if (estadoPublicado != null) {
                intercambio.getProductoSolicitado().setEstado(estadoPublicado);
                intercambio.getProductoOfrecido().setEstado(estadoPublicado);
                publicacionRepository.save(intercambio.getProductoSolicitado());
                publicacionRepository.save(intercambio.getProductoOfrecido());
            }
            intercambio.setEstadoIntercambio(Intercambio.EstadoIntercambio.CANCELADO);
        }

        return intercambioRepository.save(intercambio);
    }

    public List<IntercambioResponseDTO> getOfertasRecibidas(Long userId) {
        List<Intercambio> intercambios = intercambioRepository.findOfertasRecibidas(userId);
        return intercambios.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<IntercambioResponseDTO> getOfertasEnviadas(Long userId) {
        List<Intercambio> intercambios = intercambioRepository.findOfertasEnviadas(userId);
        return intercambios.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    private IntercambioResponseDTO convertToResponseDTO(Intercambio intercambio) {
        IntercambioResponseDTO dto = new IntercambioResponseDTO();
        dto.setId(intercambio.getId());
        dto.setProductoSolicitado(intercambio.getProductoSolicitado());
        dto.setProductoOfrecido(intercambio.getProductoOfrecido());
        dto.setIdUsuarioSolicitante(intercambio.getIdUsuarioSolicitante());
        dto.setIdUsuarioPropietario(intercambio.getIdUsuarioPropietario());
        dto.setEstadoIntercambio(intercambio.getEstadoIntercambio());
        dto.setFechaCreacion(intercambio.getFechaCreacion());
        dto.setFechaRespuesta(intercambio.getFechaRespuesta());
        dto.setConfirmacionSolicitante(intercambio.getConfirmacionSolicitante());
        dto.setConfirmacionPropietario(intercambio.getConfirmacionPropietario());
        
        // Aquí podrías agregar lógica para obtener los nombres de usuario
        // desde el servicio de usuarios si es necesario
        dto.setNombreUsuarioSolicitante("Usuario " + intercambio.getIdUsuarioSolicitante());
        dto.setNombreUsuarioPropietario("Usuario " + intercambio.getIdUsuarioPropietario());
        
        return dto;
    }
} 