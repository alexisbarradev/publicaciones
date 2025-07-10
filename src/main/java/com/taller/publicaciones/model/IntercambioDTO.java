package com.taller.publicaciones.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntercambioDTO {
    private Long idProductoSolicitado;
    private Long idProductoOfrecido;
    private Long idUsuarioSolicitante;
    private Long idUsuarioPropietario;
} 