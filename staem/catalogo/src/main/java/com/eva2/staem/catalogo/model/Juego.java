package com.eva2.staem.catalogo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "juegos_catalogo")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Juego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El titulo es obligatorio")
    @Size(max = 100, message = "El titulo no puede superar 100 caracteres")
    @Column(nullable = false)
    private String titulo;

    @NotBlank(message = "La descripcion es obligatoria")
    @Size(max = 1000, message = "La descripcion no puede superar 1000 caracteres")
    @Column(nullable = false, length = 1000)
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo")
    @Column(nullable = false)
    private Double precio;

    @NotBlank(message = "El genero es obligatorio")
    @Size(max = 50, message = "El genero no puede superar 50 caracteres")
    private String genero;

    @NotBlank(message = "El desarrollador es obligatorio")
    @Size(max = 100, message = "El desarrollador no puede superar 100 caracteres")
    private String desarrollador;

    @Size(max = 255, message = "La URL de imagen no puede superar 255 caracteres")
    private String imagenUrl;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private Boolean disponible;
}