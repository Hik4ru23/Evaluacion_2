package com.eva2.staem.biblioteca.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BibliotecaResponseDTO {
    private Long id;
    private Long usuarioId;
    private Long juegoId;
    private LocalDateTime fechaAdquisicion;
}
