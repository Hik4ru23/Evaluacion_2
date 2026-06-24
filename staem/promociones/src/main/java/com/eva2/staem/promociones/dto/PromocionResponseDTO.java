package com.eva2.staem.promociones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromocionResponseDTO {
    private Long id;
    
    private Long juegoId;
    
    private Double porcentajeDescuento;
    
    private LocalDateTime fechaInicio;
    
    private LocalDateTime fechaFin;
}
