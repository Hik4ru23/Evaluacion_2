package com.eva2.staem.soporte.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponseDTO {
    private Long id;
    
    private Long usuarioId;
    
    private String asunto;
    
    private String descripcion;
    
    private String estado;
    
    private LocalDateTime fechaCreacion;
}
