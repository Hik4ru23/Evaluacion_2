package com.eva2.staem.resenas.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResenaRequestDTO {
    @NotNull
    private Long usuarioId;

    @NotNull
    private Long juegoId;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer calificacion;

    @NotBlank
    private String comentario;
}