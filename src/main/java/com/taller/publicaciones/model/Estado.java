package com.taller.publicaciones.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "estado")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Estado {
    @Id
    private Integer id;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
} 