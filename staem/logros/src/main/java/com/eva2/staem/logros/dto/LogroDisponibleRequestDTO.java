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
public class LogroDisponibleRequestDTO {

    @NotNull
    private Long juegoId;

    @NotBlank
    private String nombre;

    private String descripcion;

    @NotNull
    @Min(1)
    private Integer puntosXp;
}
