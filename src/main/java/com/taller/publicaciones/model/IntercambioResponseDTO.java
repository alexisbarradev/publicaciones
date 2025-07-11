package com.taller.publicaciones.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntercambioResponseDTO {
    private Long id;
    private Publicacion productoSolicitado;
    private Publicacion productoOfrecido;
    private Long idUsuarioSolicitante;
    private Long idUsuarioPropietario;
    private String nombreUsuarioSolicitante;
    private String nombreUsuarioPropietario;
    private Intercambio.EstadoIntercambio estadoIntercambio;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaRespuesta;
    private Intercambio.ConfirmacionEstado confirmacionSolicitante;
    private Intercambio.ConfirmacionEstado confirmacionPropietario;
} 