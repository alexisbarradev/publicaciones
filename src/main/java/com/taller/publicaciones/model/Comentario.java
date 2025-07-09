package com.taller.publicaciones.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "comentarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "El texto del comentario es obligatorio")
    private String texto;

    @Column(name = "id_autor", nullable = false)
    @NotNull(message = "El ID del autor es obligatorio")
    private Long idAutor;

    @Column(name = "id_publicacion", nullable = false)
    @NotNull(message = "El ID de la publicación es obligatorio")
    private Long idPublicacion;

    @Column(name = "valoracion", nullable = false)
    @NotNull(message = "La valoración es obligatoria")
    @Min(value = 1, message = "La valoración debe ser al menos 1")
    @Max(value = 5, message = "La valoración debe ser máximo 5")
    private Integer valoracion;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_publicacion", insertable = false, updatable = false)
    private Publicacion publicacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
} 