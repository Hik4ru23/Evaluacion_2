package com.eva2.staem.catalogo.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JuegoResponseDTO {

    private Long id;
    private String titulo;
    private String descripcion;
    private Double precio;
    private String genero;
    private String desarrollador;
    private String imagenUrl;
    private Integer stock;
    private Boolean disponible;
}
