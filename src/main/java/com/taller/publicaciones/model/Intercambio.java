package com.taller.publicaciones.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "intercambios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Intercambio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto_solicitado", nullable = false)
    @NotNull(message = "El producto solicitado es obligatorio")
    private Publicacion productoSolicitado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto_ofrecido", nullable = false)
    @NotNull(message = "El producto ofrecido es obligatorio")
    private Publicacion productoOfrecido;

    @Column(name = "id_usuario_solicitante", nullable = false)
    @NotNull(message = "El ID del usuario solicitante es obligatorio")
    private Long idUsuarioSolicitante;

    @Column(name = "id_usuario_propietario", nullable = false)
    @NotNull(message = "El ID del usuario propietario es obligatorio")
    private Long idUsuarioPropietario;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_intercambio", nullable = false)
    private EstadoIntercambio estadoIntercambio = EstadoIntercambio.PENDIENTE;

    @Enumerated(EnumType.STRING)
    @Column(name = "confirmacion_solicitante", nullable = false)
    private ConfirmacionEstado confirmacionSolicitante = ConfirmacionEstado.PENDIENTE;

    @Enumerated(EnumType.STRING)
    @Column(name = "confirmacion_propietario", nullable = false)
    private ConfirmacionEstado confirmacionPropietario = ConfirmacionEstado.PENDIENTE;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }

    public enum EstadoIntercambio {
        PENDIENTE,
        ACEPTADO,
        RECHAZADO,
        CANCELADO
    }

    public enum ConfirmacionEstado {
        PENDIENTE,
        CONFIRMADO,
        REVERTIDO
    }
} 