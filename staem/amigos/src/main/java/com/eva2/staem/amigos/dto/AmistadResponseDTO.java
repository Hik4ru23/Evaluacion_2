package com.eva2.staem.amigos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmistadResponseDTO {
    private Long id;
    
    private Long usuarioId;
    
    private Long amigoId;
    
    private String estado;
    
    private LocalDateTime fechaSolicitud;
}
