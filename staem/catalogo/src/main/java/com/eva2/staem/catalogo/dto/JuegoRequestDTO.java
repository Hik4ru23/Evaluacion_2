package com.eva2.staem.catalogo.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JuegoRequestDTO {

    @NotBlank(message = "El titulo es obligatorio")
    @Size(max = 100, message = "El titulo no puede superar 100 caracteres")
    private String titulo;

    @NotBlank(message = "La descripcion es obligatoria")
    @Size(max = 1000, message = "La descripcion no puede superar 1000 caracteres")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    private Double precio;

    @NotBlank(message = "El genero es obligatorio")
    @Size(max = 50)
    private String genero;

    @NotBlank(message = "El desarrollador es obligatorio")
    @Size(max = 100)
    private String desarrollador;

    @Size(max = 255)
    private String imagenUrl;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;
}
