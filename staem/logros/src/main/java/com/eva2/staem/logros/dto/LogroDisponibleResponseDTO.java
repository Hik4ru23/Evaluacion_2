package com.eva2.staem.logros.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogroDisponibleResponseDTO {
    private Long id;
    private Long juegoId;
    private String nombre;
    private String descripcion;
    private Integer puntosXp;
}
