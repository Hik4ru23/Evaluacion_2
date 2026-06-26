package com.eva2.staem.carrito.dto;
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
    private Integer stock;
}
