package com.eva2.staem.logros.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogroRequestDTO {

    @NotNull(message = "El ID del juego es obligatorio")
    private Long juegoId;

    @NotBlank(message = "El nombre del logro no puede estar vacio")
    private String nombre;

    private String descripcion;

    @NotNull(message = "Los puntos XP son obligatorios")
    @Min(value = 1, message = "Los puntos XP deben ser mayores a 0")
    private Integer puntosXp;
}
