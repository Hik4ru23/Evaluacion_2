package com.eva2.staem.logros.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogroResponseDTO {
    private Long id;
    private Long usuarioId;
    private Long juegoId;
    private String nombre;
    private Integer puntosXp;
}
