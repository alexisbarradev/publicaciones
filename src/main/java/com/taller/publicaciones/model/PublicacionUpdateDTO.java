package com.taller.publicaciones.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicacionUpdateDTO {
    private String titulo;
    private String descripcion;
    private Integer precio;
    private String urlFoto;
    private Estado estado;
    // No incluimos idAutor ni fechaCreacion ya que no deberían cambiar en una actualización
} 