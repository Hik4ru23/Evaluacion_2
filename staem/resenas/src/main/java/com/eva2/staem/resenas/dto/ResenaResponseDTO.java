package com.eva2.staem.resenas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResenaResponseDTO {
    private Long id;
    private Long usuarioId;
    private Long juegoId;
    private Integer calificacion;
    private String comentario;
    private LocalDateTime fechaResena;
}